package com.soups.spring.web.discpsched.entitie;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;

@Table(name = "rdu")
@Entity
public class Rdu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private Integer type;
    @Column(name = "topic")
    private String topic;
    @Column(name = "rsp")
    private Integer rsp;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Rdu() {
    }

    public Rdu(Integer id, String name, Integer type, String topic, Integer rsp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.topic = topic;
        this.rsp = rsp;
    }
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRsp() {
        return rsp;
    }

    public void setRsp(Integer rsp) {
        this.rsp = rsp;
    }
}
