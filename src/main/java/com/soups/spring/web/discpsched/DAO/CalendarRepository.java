package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.Calendar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CalendarRepository extends CrudRepository<Calendar, Integer> {
    Calendar findByDay (LocalDate date);
}