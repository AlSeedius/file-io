package com.soups.spring.web.discpsched.hms.pushes;

import com.soups.spring.web.discpsched.hms.pushes.ClickAction;

public class Notification{
    public String title;
    public String body;
    public ClickAction click_action;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Notification(String title, String body, ClickAction click_action){
        this.title = title;
        this.body = body;
        this.click_action = click_action;
    }
}