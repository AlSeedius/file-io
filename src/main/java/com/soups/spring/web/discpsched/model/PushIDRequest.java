package com.soups.spring.web.discpsched.model;

public class PushIDRequest {

    private Integer userId;
    private String token;
    private String topic;

    public PushIDRequest() {}

    public PushIDRequest(Integer userId, String token, String topic){
        this.userId=userId;
        this.token=token;
        this.topic=topic;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
