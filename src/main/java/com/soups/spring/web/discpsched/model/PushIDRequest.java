package com.soups.spring.web.discpsched.model;

public class PushIDRequest {

    private Integer userId;
    private String token;
    private String topic;
    private Integer deviceType;

    public PushIDRequest() {}

    public PushIDRequest(Integer userId, String token, String topic, Integer deviceType){
        this.userId=userId;
        this.token=token;
        this.topic=topic;
        this.deviceType = deviceType;
    }
    public PushIDRequest(Integer userId, String token, String topic){
        this.userId=userId;
        this.token=token;
        this.topic=topic;
        this.deviceType = 1;
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

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }
}
