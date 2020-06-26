package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.entitie.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;

    public Iterable<Person> foundPersons() {
        return personRepository.findAll();
    }

    public Person onePerson(Integer id) {
        return personRepository.findById(id).get();
    }

}
