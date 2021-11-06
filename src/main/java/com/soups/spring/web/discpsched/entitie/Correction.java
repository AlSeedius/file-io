package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;

public class Correction {
    public LocalDate date;
    public String name;

    public Correction() {
    }

    public Correction(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
