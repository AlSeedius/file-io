package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;

public class Callback10Shifts {


    private String shift1;
    private String shift2;
    private String types;
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getShift1() {
        return shift1;
    }

    public void setShift1(String shift1) {
        this.shift1 = shift1;
    }

    public String getShift2() {
        return shift2;
    }

    public void setShift2(String shift2) {
        this.shift2 = shift2;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Callback10Shifts(String shift1, String shift2, String types, LocalDate date){
        this.shift1=shift1;
        this.shift2=shift2;
        this.types=types;
        this.date = date;
    }
    public Callback10Shifts(){}
}
