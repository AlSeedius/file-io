package com.soups.spring.web.discpsched.entitie;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "date")
@Entity
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "day")
    private LocalDate day;

    public Calendar() {}

    public LocalDate getDay() {
        return day;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Calendar(LocalDate day){
        this.day = day;
    }
}
