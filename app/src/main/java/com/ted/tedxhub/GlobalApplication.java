package com.ted.tedxhub;

import android.app.Application;

/**
 * Created with IntelliJ IDEA.
 * User: Raghav
 * Date: 12/31/13
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class GlobalApplication extends Application {

    private static GlobalApplication sInstance;

    public String ApplicationUrl;
    public String ApplicationServicesUrl;
    public String CaseAddUrl;
    public String CaseAttachmentAddUrl;
    public String UserServiceUrl;
    public String ValidateCredentialsUrl;
    public String NewsFeedUrl;
    public String NotificationsUrl;
    public String SecuredLoginUrl;
    public String MyAccountNotificationsUrl;
    public String MyAccountMessagesUrl;
    public String UnreadMessagesUrl;
    public static GlobalApplication getInstance() {
        return sInstance;
    }
    public Integer NotificationPingInterval;
    public String ApplicationDetailsUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
    }

    protected void initializeInstance() {
        // do all you initialization here
        NotificationPingInterval=60000;
        //ApplicationName="Communifire";
        //ApplicationUrl="http://mylocality.in";
        ApplicationUrl="";
        ApplicationServicesUrl= String.format("%s/services", ApplicationUrl);
        UserServiceUrl= String.format("%s/userservice.svc", ApplicationServicesUrl);
        ValidateCredentialsUrl= String.format("%s/users/user/restapikey?username=%%s&password=%%s&format=json", UserServiceUrl);
        CaseAddUrl = String.format("%s/issuetrackerservice.svc/cases", ApplicationServicesUrl);
        CaseAttachmentAddUrl= String.format("%s?issueid=%%s&fileName=%%s", String.format("%s/issuetrackerservice.svc/attachment", ApplicationServicesUrl));
        NewsFeedUrl = String.format("%s/CommonService.svc/activity",ApplicationServicesUrl);
        NotificationsUrl = String.format("%s/NotificationsService.svc/notifications/unread?lastNotificationID=%%s&startPage=%%s&numberOfRecords=%%s", ApplicationServicesUrl);
        UnreadMessagesUrl = String.format("%s/InboxService.svc/messages/unread?lastMessageID=%%s&startPage=%%s&numberOfRecords=%%s", ApplicationServicesUrl);
        SecuredLoginUrl = String.format("%s/securedlogin", ApplicationUrl);
        MyAccountNotificationsUrl = String.format("%s/myaccount/notifications", ApplicationUrl);
        MyAccountMessagesUrl = String.format("%s/chat/threads/", ApplicationUrl);
        ApplicationDetailsUrl = String.format("%s/CommonService.svc/application/details", ApplicationServicesUrl);
    }
}

