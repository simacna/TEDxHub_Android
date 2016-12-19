package com.ted.tedxhub;

/**
 * Created by raghav on 19-01-2015.
 */
public class MessageDto {
    private String dateSent, senderAvatarImageURL, senderUserDisplayName, messageBody, messageID;

    public MessageDto(){}

    public MessageDto(String dateSent, String senderAvatarImageURL, String senderUserDisplayName, String messageBody) {
        this.dateSent = dateSent;
        this.senderAvatarImageURL = senderAvatarImageURL;
        this.senderUserDisplayName = senderUserDisplayName;
        this.messageBody = messageBody;
    }


    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSenderUserDisplayName() {

        return senderUserDisplayName;
    }

    public void setSenderUserDisplayName(String senderUserDisplayName) {
        this.senderUserDisplayName = senderUserDisplayName;
    }

    public String getSenderAvatarImageURL() {

        return senderAvatarImageURL;
    }

    public void setAvatarImageUrl(String senderAvatarImageURL) {
        this.senderAvatarImageURL = senderAvatarImageURL;
    }

    public String getDateSent() {
        return dateSent;
    }

    public String getmessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }
}

