package com.soups.spring.web.discpsched.entitie;

import javax.persistence.*;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "token")
    private String token;
    @Column(name = "app_id")
    private Integer appID;
    @Column(name = "device_type")
    private Integer deviceType;

    public User(){}

    public User(String token, Integer appId, Integer deviceType){
        this.token=token;
        this.appID=appId;
        this.deviceType = deviceType;
    }

    public User(String token, Integer appId){
        this.token=token;
        this.appID=appId;
        this.deviceType = 1;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }
}
