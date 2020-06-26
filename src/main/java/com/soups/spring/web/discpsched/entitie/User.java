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

    public User(){}

    public User(String token, Integer appId){
        this.token=token;
        this.appID=appId;
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
}
