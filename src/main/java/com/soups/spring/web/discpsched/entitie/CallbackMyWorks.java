package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;
public class CallbackMyWorks {
    private String type;
    private LocalDate date;
    public CallbackMyWorks(){}
    public CallbackMyWorks(String type, LocalDate date) {
        this.type = type;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
