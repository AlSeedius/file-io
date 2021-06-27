package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Callback8 {

    private Integer daysToWeekend;
    private Integer daysOfWeekend;
    private LocalDate dateOfWork;

    public Callback8(){}

    public Callback8(Integer daysToWeekend, Integer daysOfWeekend, LocalDate dateOfWork) {
        this.daysToWeekend = daysToWeekend;
        this.daysOfWeekend = daysOfWeekend;
        this.dateOfWork = dateOfWork;
    }

    public Integer getDaysToWeekend() {
        return daysToWeekend;
    }

    public void setDaysToWeekend(Integer daysToWeekend) {
        this.daysToWeekend = daysToWeekend;
    }

    public Integer getDaysOfWeekend() {
        return daysOfWeekend;
    }

    public void setDaysOfWeekend(Integer daysOfWeekend) {
        this.daysOfWeekend = daysOfWeekend;
    }

    public LocalDate getDateOfWork() {
        return dateOfWork;
    }

    public void setDateOfWork(LocalDate dateOfWork) {
        this.dateOfWork = dateOfWork;
    }
}
