package com.soups.spring.web.discpsched.entitie;

import javax.persistence.*;

@Table(name = "schedule")
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "date_id")
    private Integer dateId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "person_id")
    private Integer personId;
    @Column(name = "type")
    private String type;

    public Schedule(){}
    public Schedule(Integer person_id, Integer date_id, String type){
        this.dateId =date_id;
        this.personId =person_id;
        this.type=type;
    }

    public Integer getDateId() {
        return dateId;
    }

    public void setDateId(Integer dateId) {
        this.dateId = dateId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
