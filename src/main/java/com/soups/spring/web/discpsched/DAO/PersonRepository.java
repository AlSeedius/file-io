package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    List<Person> findByFirstName(String firstName);
    //List<Person> findBySecondName(String secondName);

    //Person findByLastName(String lastName);
    //Person findByLastNameAndRduId(String lastName, Integer rduId);
    Person findByLastNameAndRduIdAndFirstNameAndSecondName(String lastName, Integer rduId, String firstName, String secondName);
}
