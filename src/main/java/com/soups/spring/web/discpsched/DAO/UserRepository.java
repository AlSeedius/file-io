package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByToken(String token);
    List<User> findByAppID(Integer id);
}
