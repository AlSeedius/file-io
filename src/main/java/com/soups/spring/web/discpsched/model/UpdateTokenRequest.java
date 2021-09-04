package com.soups.spring.web.discpsched.model;

public class UpdateTokenRequest {

    private String newToken;
    private String oldToken;

    public UpdateTokenRequest() {}

    public UpdateTokenRequest(String newToken, String oldToken) {
        this.newToken = newToken;
        this.oldToken = oldToken;
    }

    public String getNewToken() {
        return newToken;
    }

    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

    public String getOldToken() {
        return oldToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }
}
