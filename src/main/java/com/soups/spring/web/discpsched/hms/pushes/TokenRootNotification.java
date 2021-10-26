package com.soups.spring.web.discpsched.hms.pushes;

import com.soups.spring.web.discpsched.hms.pushes.Message;

import java.util.List;

public class TokenRootNotification {
    public boolean validate_only=false;
    public Message message;

    public TokenRootNotification(String body, String header, List<String> tokens) {
        Message msg = new Message();
        Notification ntf1 = new Notification(header, body);
        Notification ntf2 = new Notification(header, body, new ClickAction());
        msg.notification = ntf1;
        msg.android = new Android(ntf2);
        msg.token = tokens;
        this.message = msg;
    }
}
