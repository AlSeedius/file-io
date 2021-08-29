package com.soups.spring.web.discpsched.controller;

import com.soups.spring.web.discpsched.entitie.Person;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("persons")
public class PersonController {

    @Autowired
    PersonService personService;

    @Autowired


    @GetMapping("/all")
    public Iterable<Person> allNames(){
        return personService.foundPersons();
    }

    @GetMapping("/{userId}")
    public Person onePerson(@PathVariable String userId){
        return personService.onePerson(Integer.parseInt(userId));
    }

    @GetMapping("/rdu")
    public Iterable<Rdu> rduList() {return personService.rduName();}

    @GetMapping("/rdu/{rduId}")
    public Iterable<Person> rduIdList(@PathVariable Integer rduId) {return personService.rduPersons(rduId);}

}
