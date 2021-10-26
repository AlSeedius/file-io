package com.soups.spring.web.discpsched.hms.pushes;

public class ClickAction{
    public int type;
    public String intent;

    public ClickAction() {
        this.type = 1;
        this.intent = "intent://com.huawei.codelabpush/deeplink?#Intent;scheme=pushscheme;launchFlags=0x04000000;i.age=180;S.name=abc;end";
    }
}