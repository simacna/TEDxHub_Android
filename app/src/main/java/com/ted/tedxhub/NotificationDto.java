package com.ted.tedxhub;

import android.graphics.Bitmap;

/**
 * Created by User on 04-09-2014.
 */
public class NotificationDto {

    private String actionText, dateCreated, avatarImageUrl, fromUser, subject, notificationID;
    private Bitmap userAvatarImage;

    public NotificationDto(){}

    public NotificationDto(String actionText, String dateCreated, String avatarImageUrl, String fromUser, String subject, Bitmap userAvatarImage) {
        this.actionText = actionText;
        this.dateCreated = dateCreated;
        this.avatarImageUrl = avatarImageUrl;
        this.fromUser = fromUser;
        this.subject = subject;
        this.userAvatarImage = userAvatarImage;
    }

    public Bitmap getUserAvatarImage() {
        return userAvatarImage;
    }

    public void setUserAvatarImage(Bitmap userAvatarImage) {
        this.userAvatarImage = userAvatarImage;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getNotificationId() {
        return notificationID;
    }

    public void setNotificationId(String notificationID) {
        this.notificationID = notificationID;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
