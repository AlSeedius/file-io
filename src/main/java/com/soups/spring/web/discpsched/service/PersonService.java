package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.RduRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.entitie.Person;
import com.soups.spring.web.discpsched.entitie.Rdu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    RduRepository rduRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    CalendarRepository calendarRepository;

    public Iterable<Person> foundPersons() {
        return personRepository.findAll();
    }

    public Person onePerson(Integer id) {
        return personRepository.findById(id).get();
    }

    public Iterable<Person> rduPersons(Integer rduId) {
        return personRepository.findByRduId(rduId);
    }

    public Iterable<Person> rduPersons(Integer rduId, Byte k) {
        Iterable<Person> persons = personRepository.findByRduId(rduId);
        List<Person> returnPersons = new ArrayList<>();
        for (Person p : persons) {
            if (scheduleRepository.findByDateIdGreaterThanEqualAndPersonId(calendarRepository.findByDay(LocalDate.now()).getId() - 30, p.getId()).size() != 0)
                returnPersons.add(p);
        }
        return returnPersons;
    }

    public Iterable<Rdu> rduName() {
        return rduRepository.findAll();
    }

}

