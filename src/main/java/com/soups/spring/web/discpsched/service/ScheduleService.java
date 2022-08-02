package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.entitie.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        } else {
            found = true;
        }
        list.sort(Comparator.comparingInt(Schedule::getDateId));
        return list.get(0);
    }

    public Callback8 callback8(Integer personId, String date) {
        Callback8 cb = new Callback8();
        int n=-1;
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId() + 1;
        List<Schedule> scheduleList = scheduleRepository.findByDateIdGreaterThanEqualAndPersonId(dateId, personId);
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getType().equals("8")){
                if (!(scheduleList.get(i).getDateId()==dateId && LocalDateTime.now().getHour()>14)) {
                    n = i;
                    break;
                }
            }
        }
        if (n!=-1){
            dateId = scheduleList.get(n).getDateId();
            List<String> typeList = new ArrayList<>();
            typeList.add("8");
            Person me = personRepository.findById(personId).get();
            cb.setDateOfWork(calendarRepository.findById(dateId).get().getDay());
            cb.setColleagues(colleagues78List(dateId, typeList, me.getRduId(), me.getLastName()));
        }
        else
        {
            cb.setDateOfWork(LocalDate.of(2000,1,1));
        }
        return cb;
    }

    public CallbackShift callbackShift(Schedule actShift, boolean found, String personID) {
        Person person = personRepository.findById(Integer.parseInt(personID)).get();
        CallbackShift tempCallBack = new CallbackShift();
        List<Schedule> thisDayList = scheduleRepository.findByDateIdAndType(actShift.getDateId(), actShift.getType());
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

    public List<CallbackRDUList> RDUList(int nRDU, String date, int nCols) {
        int dateId = calendarRepository.findByDay(LocalDate.parse(date)).getId() + 1;
        List<CallbackRDUList> callback = new ArrayList<>();
        for (Person p : personRepository.findByRduId(nRDU)) {
            CallbackRDUList cb = new CallbackRDUList(nCols);
            cb.setName(p.getLastName() + " " + p.getFirstName() + ". " + p.getSecondName() + ".");
            List<Schedule> schedList = scheduleRepository.findByDateIdGreaterThanEqualAndDateIdLessThanEqualAndPersonId(dateId, dateId + nCols - 1, p.getId());
            for (int i = 0; i < nCols; i++) {
                for (Schedule s : schedList) {
                    if (s.getDateId() == (i + dateId)) {
                        cb.addTypes(i, s.getType());
                        break;
                    }
                }
                if (cb.getTypes()[i] == null)
                    cb.addTypes(i, "0");
            }
            callback.add(cb);
        }
        if (nRDU==4){  //костыль для разделения по службам в СОПР ОДУ Центра
            List<CallbackRDUList> newCallBack = new ArrayList<>();
            newCallBack.add(callback.get(0));
            newCallBack.add(callback.get(1));
            newCallBack.add(callback.get(2));
            newCallBack.add(callback.get(callback.size()-2));
            newCallBack.add(callback.get(3));
            newCallBack.add(callback.get(4));
            newCallBack.add(callback.get(callback.size()-1));
            for (int i=5; i<callback.size()-2; i++){
                newCallBack.add(callback.get(i));
            }
            return newCallBack;
        }
        return callback;
    }

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

    private String colleagues78List(int dateId, List<String> type, Integer nRDU, String myLastname) {
        String tempString = "";
        Person tempPerson;
        for (String t : type) {
            List<Schedule> thisDayList = scheduleRepository.findByDateIdAndType(dateId, t);
            for (int i = 0; i < thisDayList.size(); i++) {
                tempPerson = personRepository.findById(thisDayList.get(i).getPersonId()).get();
                if (tempPerson.getRduId() == nRDU && tempPerson.getLastName()!=myLastname)
                    tempString += tempPerson.getLastName() + " " + tempPerson.getFirstName() + ". "+ tempPerson.getSecondName()+"., ";
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
