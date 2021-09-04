package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;
import java.util.List;

public class Callback8 {

    private LocalDate dateOfWork;
    private String colleagues;

    public Callback8(){}

    public Callback8(LocalDate dateOfWork, String colleagues) {
        this.dateOfWork = dateOfWork;
        this.colleagues = colleagues;
    }

    public String getColleagues() {
        return colleagues;
    }

    public void setColleagues(String colleagues) {
        this.colleagues = colleagues;
    }

    public LocalDate getDateOfWork() {
        return dateOfWork;
    }

    public void setDateOfWork(LocalDate dateOfWork) {
        this.dateOfWork = dateOfWork;
    }
}
