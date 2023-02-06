package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;
public class CallbackMyWorks {
    private String type;
    private LocalDate date;

    private Integer placeNum;
    private Integer currentPlace;
    public CallbackMyWorks(){}
    public CallbackMyWorks(String type, LocalDate date, Integer placeNum, Integer currentPlace) {
        this.type = type;
        this.date = date;
        this.placeNum = placeNum;
        this.currentPlace = currentPlace;
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

    public Integer getPlaceNum() {
        return placeNum;
    }

    public void setPlaceNum(Integer placeNum) {
        this.placeNum = placeNum;
    }

    public Integer getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Integer currentPlace) {
        this.currentPlace = currentPlace;
    }
}
