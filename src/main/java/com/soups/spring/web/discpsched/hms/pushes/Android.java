package com.soups.spring.web.discpsched.hms.pushes;

public class Android{
    public Notification notification;

    public Android(Notification notification) {
        this.notification = notification;
    }

    public Android() {
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}