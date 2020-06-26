package com.soups.spring.web.discpsched.DAO;

import com.soups.spring.web.discpsched.entitie.Schedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Integer> {

    List<Schedule> findByDateIdGreaterThanEqualAndPersonId(Integer dateId, Integer personId);
    List<Schedule> findByDateIdGreaterThanEqualAndDateIdLessThanEqualAndPersonId(Integer dateMinId, Integer dateMaxId, Integer personId);
    List<Schedule> findByDateIdGreaterThanEqualAndPersonIdAndTypeEqualsOrDateIdGreaterThanEqualAndPersonIdAndTypeEquals(Integer dateId, Integer personId, String Type, Integer dateId2, Integer personId2, String Type2);
    List<Schedule> findByDateIdAndType(Integer dateId, String type);
    List<Schedule> findByDateId(Integer dateId);
    List<Schedule> findByDateIdAndPersonId(Integer dateId, Integer personId);
}

