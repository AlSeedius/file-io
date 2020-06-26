package com.soups.spring.web.discpsched.entitie;

import javax.persistence.*;

@Table(name = "person")
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "secondName")
    private String secondName;
    @Column(name = "rdu_id")
    private Integer rduId;

    public Person() {}

    public Person(String firstName, String lastName, String secondName, Integer rduId) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.secondName=secondName;
        this.rduId=rduId;
    }

    public Integer getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Integer getRduId() {
        return rduId;
    }

    public void setRduId(Integer rduId) {
        this.rduId = rduId;
    }
}
