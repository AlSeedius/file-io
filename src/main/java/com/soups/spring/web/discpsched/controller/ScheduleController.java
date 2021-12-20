package com.soups.spring.web.discpsched.controller;

import com.soups.spring.web.discpsched.entitie.*;
import com.soups.spring.web.discpsched.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.callback.Callback;
import java.util.List;

@RestController
@RequestMapping("shifts")
public class ScheduleController {
    @Autowired
    ScheduleService shiftService;

    @GetMapping("/version")
    public String getVersion() {
        return "0.86";
    }

    @GetMapping("/10shifts/{date}/{personID}")
    public List<Callback10Shifts> scheduleList(@PathVariable String date, @PathVariable String personID) {
        return shiftService.callback10ShiftsList(date, personID);
    }

    @GetMapping("/calendar/rduid/{date}/{rduId}")
    public Callback10Shifts calendarRduSchedule(@PathVariable String date, @PathVariable String rduId) {
        return shiftService.callbackCalendarRdu(date, rduId);
    }

    @GetMapping("/oneshift/{date}/{personID}")
    public CallbackShift oneSchedule(@PathVariable String date, @PathVariable String personID) {
        return shiftService.callbackShift(shiftService.oneSchedule(date, personID), shiftService.isFound(), personID);
    }

    @GetMapping("/one8/{date}/{personID}")
    public Callback8 one8(@PathVariable String date, @PathVariable Integer personID) {
        return shiftService.callback8(personID, date);
    }

    @GetMapping("/myworks/{date}/{personID}")
    public List<CallbackMyWorks> workList(@PathVariable String date, @PathVariable String personID) {
        return shiftService.callbackWorksList(date, personID);
    }

    @GetMapping("/month/{date}/{personID}")
    public List<CallbackMyWorks> monthworkList(@PathVariable String date, @PathVariable String personID) {
        return shiftService.callbackMonthList(date, personID);
    }

    @GetMapping("/list/{rduId}/{date}/{nCols}")
    public Iterable<CallbackRDUList> rduSchedList(@PathVariable Integer rduId, @PathVariable String date, @PathVariable Integer nCols) {
        return shiftService.RDUList(rduId, date, nCols);
    }
}
