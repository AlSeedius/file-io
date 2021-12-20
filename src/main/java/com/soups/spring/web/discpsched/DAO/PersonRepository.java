package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    List<Person> findByRduId(Integer rduId);
    Person findByLastNameAndRduIdAndFirstNameAndSecondName(String lastName, Integer rduId, String firstName, String secondName);
}
