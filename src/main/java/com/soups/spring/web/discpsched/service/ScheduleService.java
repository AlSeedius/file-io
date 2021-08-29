package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.entitie.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    CalendarRepository calendarRepository;

    private boolean found;

    public boolean isFound() {
        return found;
    }

    public Schedule oneSchedule(String date, String personID) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId()+1;
        List<Schedule> list = scheduleRepository.findByDateIdGreaterThanEqualAndPersonIdAndTypeEqualsOrDateIdGreaterThanEqualAndPersonIdAndTypeEquals
                (dateId, Integer.parseInt(personID), "1", dateId, Integer.parseInt(personID), "2");
        if (list.isEmpty()) {
            found = false;
            list = scheduleRepository.findByDateIdGreaterThanEqualAndPersonId(dateId, Integer.parseInt(personID));
            list.sort(Comparator.comparingInt(Schedule::getDateId));
            return list.get(0);
        } else {
            found = true;
            list.sort(Comparator.comparingInt(Schedule::getDateId));
            return list.get(0);
        }
    }
    public Callback8 callback8(String  personId, String date) {
        Callback8 cb = new Callback8();
        int j=0, k=0, n=0, zz=0, t=1;
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId() + 1;
        List<Schedule> scheduleList = scheduleRepository.findByDateIdGreaterThanEqualAndPersonId(dateId, Integer.parseInt(personId));
        if (LocalDateTime.now().getHour()>14)
            t=2;
        if (scheduleList.get(0).getType().equals("8"))
            n=1;
        for (int i = t; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getType().equals("8") && n==0)
                n=i+1;
            if (scheduleList.get(i).getType().toLowerCase().equals("о"))
                zz=i+1;
            else if ((j=(scheduleList.get(i).getDateId() - scheduleList.get(i - 1).getDateId())) > 1 && scheduleList.get(i-1).getType().equals("8")) {
                k = i;
                break;
            }
        }
        j--;
        cb.setDaysOfWeekend(j);
        cb.setDaysToWeekend(k-zz);
        if (n!=0)
            cb.setDateOfWork(calendarRepository.findById(scheduleList.get(n-1).getDateId()).get().getDay());
        else
        {
            cb.setDateOfWork(LocalDate.of(2000,1,1));
        }
        return cb;
    }
        //List<Schedule> sc = scheduleRepository.findByDateIdAndPersonId()
    public CallbackShift callbackShift(Schedule actShift, boolean found, String personID) {
        Person person = personRepository.findById(Integer.parseInt(personID)).get();
        CallbackShift tempCallBack = new CallbackShift();
        List<Schedule> thisDayList = scheduleRepository.findByDateIdAndType(actShift.getDateId(), actShift.getType());
        List<Integer> colId = null;
        String colName = "";
        for (Schedule sched : thisDayList) {
            if (sched.getPersonId() != actShift.getPersonId()) {
                Person colleague = personRepository.findById(sched.getPersonId()).get();
                if (colleague.getRduId() == person.getRduId())
                    colName += colleague.getLastName() + " " + colleague.getFirstName() + ". " + colleague.getSecondName() + "." + ", ";
            }
        }
        tempCallBack.setColleagueName(colName.substring(0, colName.length() - 2));
        tempCallBack.setShiftFound(found);
        LocalDate startDate = calendarRepository.findById(actShift.getDateId()).get().getDay();
        if (actShift.getType().equals("1")) {
            tempCallBack.setStartShift(startDate.atTime(8, 0));
            tempCallBack.setPrevShift(namesInShift(actShift.getDateId() - 1, "2", person.getRduId()));
            tempCallBack.setNextShift(namesInShift(actShift.getDateId(), "2", person.getRduId()));
        } else {
            tempCallBack.setStartShift(startDate.atTime(20, 0));
            tempCallBack.setPrevShift(namesInShift(actShift.getDateId(), "1", person.getRduId()));
            tempCallBack.setNextShift(namesInShift(actShift.getDateId() + 1, "1", person.getRduId()));
        }
        return tempCallBack;
    }

  //  public

    private String namesInShift(int dateId, String type, int nRDU) {
        List<Schedule> thisDayList = scheduleRepository.findByDateIdAndType(dateId, type);
        String sName = "";
        Person tempPerson;
        for (Schedule sched : thisDayList) {
            tempPerson = personRepository.findById(sched.getPersonId()).get();
            if (tempPerson.getRduId() == nRDU)
                sName += tempPerson.getLastName() + ", ";
        }
        if (sName.length()>0)
            return sName.substring(0, sName.length() - 2);
        else
            return "0";
    }

    private String namesIn78(int dateId, List<String> type, Integer nRDU) {
        String tempString = "";
        Person tempPerson;
        for (String t : type) {
            List<Schedule> thisDayList = scheduleRepository.findByDateIdAndType(dateId, t);
            for (int i = 0; i < thisDayList.size(); i++) {
                tempPerson = personRepository.findById(thisDayList.get(i).getPersonId()).get();
                if (tempPerson.getRduId() == nRDU)
                    tempString += tempPerson.getLastName() + ", ";
            }
        }
        if (tempString.length()>0)
            return tempString.substring(0, tempString.length() - 2);
        else
            return "0";
    }

    public List<Callback10Shifts> callback10ShiftsList(String date, String personID) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId()+1;
        List<Callback10Shifts> tempCallback = new ArrayList<>();
        for (int i = dateId - 5; i <= dateId + 5; i++) {
            if (!scheduleRepository.findByDateId(i).isEmpty())
                tempCallback.add(getFullDayInfo(i, personID));
        }
        return tempCallback;
    }

/*    public Callback10Shifts callbackCalendarId(String date, String personID) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId()+1;
        return getFullDayInfo(dateId, personID);
    }*/
    public Callback10Shifts callbackCalendarRdu(String date, String rduId) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId()+1;
        return getFullDayInfoRdu(dateId, rduId);
    }

    public List<CallbackMyWorks> callbackWorksList(String date, String personID) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId()+1;
        List<Schedule> scheduleList = scheduleRepository.findByDateIdGreaterThanEqualAndPersonId(dateId, Integer.parseInt(personID));
        scheduleList.sort(Comparator.comparingInt(Schedule::getDateId));
        List<CallbackMyWorks> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (scheduleList.get(i) != null)
                tempList.add(new CallbackMyWorks(scheduleList.get(i).getType(),
                        calendarRepository.findById(scheduleList.get(i).getDateId()).get().getDay()));
        }
        return tempList;
    }
    public List<CallbackMyWorks> callbackMonthList(String date, String personID) {
        LocalDate day = LocalDate.parse(date);
        day.getYear();
        LocalDate day1 = LocalDate.of(day.getYear(),day.getMonth(),1);
        LocalDate day2 = LocalDate.of(day.getYear(),day.getMonth(), day.lengthOfMonth());
        int dateId = calendarRepository.findByDay(day1).getId()+1;
        List<Schedule> scheduleList = scheduleRepository.findByDateIdGreaterThanEqualAndDateIdLessThanEqualAndPersonId(dateId, dateId+day.lengthOfMonth()-1, Integer.parseInt(personID));
        scheduleList.sort(Comparator.comparingInt(Schedule::getDateId));
        List<CallbackMyWorks> tempList = new ArrayList<>();
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i) != null)
                tempList.add(new CallbackMyWorks(scheduleList.get(i).getType(),
                        calendarRepository.findById(scheduleList.get(i).getDateId()).get().getDay()));
        }
        return tempList;
    }

    private Callback10Shifts getFullDayInfo(int dateId, String personID) {
        Callback10Shifts tempCallback10shifts = new Callback10Shifts();
        List<Schedule> scheduleList = scheduleRepository.findByDateId(dateId);
        List<String> type = new ArrayList<>();
        Integer nRDU = personRepository.findById(Integer.parseInt(personID)).get().getRduId();
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getType().equals("7") | scheduleList.get(i).getType().equals("8") | scheduleList.get(i).getType().equals("4")) {
                if (!type.contains(scheduleList.get(i).getType()))
                    type.add(scheduleList.get(i).getType());
            }
        }
        if (!type.isEmpty())
            tempCallback10shifts.setTypes(namesIn78(dateId, type, nRDU));
        else
            tempCallback10shifts.setTypes("0");
        tempCallback10shifts.setShift1(namesInShift(dateId, "1", nRDU));
        tempCallback10shifts.setShift2(namesInShift(dateId, "2", nRDU));
        tempCallback10shifts.setDate(calendarRepository.findById(dateId).get().getDay());
        return tempCallback10shifts;
    }
    private Callback10Shifts getFullDayInfoRdu(int dateId, String rduId) {
        Callback10Shifts tempCallback10shifts = new Callback10Shifts();
        List<Schedule> scheduleList = scheduleRepository.findByDateId(dateId);
        List<String> type = new ArrayList<>();
        Integer nRDU = Integer.valueOf(rduId);
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getType().equals("7") | scheduleList.get(i).getType().equals("8") | scheduleList.get(i).getType().equals("4")) {
                if (!type.contains(scheduleList.get(i).getType()))
                    type.add(scheduleList.get(i).getType());
            }
        }
        if (!type.isEmpty())
            tempCallback10shifts.setTypes(namesIn78(dateId, type, nRDU));
        else
            tempCallback10shifts.setTypes("0");
        tempCallback10shifts.setShift1(namesInShift(dateId, "1", nRDU));
        tempCallback10shifts.setShift2(namesInShift(dateId, "2", nRDU));
        tempCallback10shifts.setDate(calendarRepository.findById(dateId).get().getDay());
        return tempCallback10shifts;
    }
}
