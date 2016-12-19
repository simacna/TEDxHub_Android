package com.ted.tedxhub;

/**
 * Created with IntelliJ IDEA.
 * User: Raghav
 * Date: 12/29/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */

import android.annotation.TargetApi;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {

            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            DefaultHttpClient client = new DefaultHttpClient();

//Added my part

            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            client = new DefaultHttpClient(httpParams);
//End my part

            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);


            //registry.register(new Scheme("https", socketFactory, 443));

            registry = client.getConnectionManager().getSchemeRegistry();
            registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));


            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

// Set verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

// Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
// adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
// appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += String.format("?%s", paramString);
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, HTTP.UTF_8);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public class TlsSniSocketFactory implements LayeredSocketFactory {
        private static final String TAG = "davdroid.SNISocketFactory";

        final  HostnameVerifier hostnameVerifier = new StrictHostnameVerifier();


        // Plain TCP/IP (layer below TLS)

        @Override
        public Socket connectSocket(Socket s, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket() throws IOException {
            return null;
        }

        @Override
        public boolean isSecure(Socket s) throws IllegalArgumentException {
            if (s instanceof SSLSocket)
                return ((SSLSocket)s).isConnected();
            return false;
        }


        // TLS layer

        @Override
        public Socket createSocket(Socket plainSocket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            if (autoClose) {
                // we don't need the plainSocket
                plainSocket.close();
            }

            // create and connect SSL socket, but don't do hostname/certificate verification yet
            SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
            SSLSocket ssl = (SSLSocket)sslSocketFactory.createSocket(InetAddress.getByName(host), port);

            // enable TLSv1.1/1.2 if available
            // (see https://github.com/rfc2822/davdroid/issues/229)
            ssl.setEnabledProtocols(ssl.getSupportedProtocols());

            // set up SNI before the handshake
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.v("Setting SNi hostname", "Setting SNI hostname");
                sslSocketFactory.setHostname(ssl, host);
            } else {
                Log.d("Error2", "No documented SNI support on Android <4.2, trying with reflection");
                try {
                    java.lang.reflect.Method setHostnameMethod = ssl.getClass().getMethod("setHostname", String.class);
                    setHostnameMethod.invoke(ssl, host);
                } catch (Exception e) {
                    Log.v("Error 3", "SNI not useable", e);
                }
            }

            // verify hostname and certificate
            SSLSession session = ssl.getSession();
            if (!hostnameVerifier.verify(host, session))
                throw new SSLPeerUnverifiedException("Cannot verify hostname: " + host);

            Log.v("Error 4", "Established " + session.getProtocol() + " connection with " + session.getPeerHost() +
                    " using " + session.getCipherSuite());

            return ssl;
        }
    }
}

