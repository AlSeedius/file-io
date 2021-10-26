package com.soups.spring.web.discpsched.hms.pushes;

import java.util.List;

public class Message{
    public Notification notification;
    public Android android;
    public List<String> token;
    public String topic;

    public Message() {
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Android getAndroid() {
        return android;
    }

    public void setAndroid(Android android) {
        this.android = android;
    }

    public List<String> getToken() {
        return token;
    }

    public void setToken(List<String> token) {
        this.token = token;
    }

    public void addToken(String token){
        this.token.add(token);
    }
}
