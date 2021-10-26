package com.soups.spring.web.discpsched.hms.pushes;

import java.util.List;

public class TopicRootNotification {
    public boolean validate_only=false;
    public Message message;

    public TopicRootNotification(String body, String header, String topic) {
        Message msg = new Message();
        Notification ntf1 = new Notification(header, body);
        Notification ntf2 = new Notification(header, body, new ClickAction());
        msg.notification = ntf1;
        msg.android = new Android(ntf2);
        msg.topic = topic;
        this.message = msg;
    }
}
