package com.soups.spring.web.discpsched.entitie;

import java.util.ArrayList;
import java.util.List;

public class CallbackRDUList {
    private String name;
    private String[] types;

    public CallbackRDUList(String name, String[] types) {
        this.name = name;
        this.types = types;
    }

    public CallbackRDUList() {
    }

    public CallbackRDUList(int n){
        types = new String[n];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public void addTypes(int n, String type){
        this.types[n] = type;
    }
}
