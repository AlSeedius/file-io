package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.Rdu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RduRepository extends CrudRepository<Rdu, Integer> {
    Rdu findByName(String name);
    //Rdu findById (Integer id);
}
