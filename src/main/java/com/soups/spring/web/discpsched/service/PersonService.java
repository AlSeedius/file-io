package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.RduRepository;
import com.soups.spring.web.discpsched.entitie.Callback10Shifts;
import com.soups.spring.web.discpsched.entitie.Person;
import com.soups.spring.web.discpsched.entitie.Rdu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public Iterable<Person> rduPersons(Integer rduId){ return personRepository.findByRduId(rduId);}
    @Autowired
    RduRepository rduRepository;

    public Iterable<Rdu> rduName() {
        List<String> names = new ArrayList<>();
        return rduRepository.findAll();
  /*      Collections.sort(rduList, ((o1, o2) -> o1.));
        rduList.
        for (Rdu rdu : rduList)
            names.add(rdu.getName());
        return names;*/
    }

}

