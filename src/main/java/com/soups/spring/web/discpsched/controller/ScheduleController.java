package com.soups.spring.web.discpsched.controller;

import com.soups.spring.web.discpsched.entitie.Callback10Shifts;
import com.soups.spring.web.discpsched.entitie.CallbackMyWorks;
import com.soups.spring.web.discpsched.entitie.CallbackShift;
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
  /*  @GetMapping("/{date}/{personID}")
    public Schedule oneSchedule(@PathVariable String date, @PathVariable String personID){
        return shiftService.oneSchedule(date, personID);
    }*/

  @GetMapping("/10shifts/{date}/{personID}")
  public List<Callback10Shifts> scheduleList(@PathVariable String date, @PathVariable String personID){
      return shiftService.callback10ShiftsList(date, personID);
  }
    @GetMapping("/calendar/{date}/{personID}")
    public Callback10Shifts calendarSchedule(@PathVariable String date, @PathVariable String personID){
        return shiftService.callbackCalendar(date, personID);
    }
  @GetMapping("/oneshift/{date}/{personID}")
  public CallbackShift oneSchedule(@PathVariable String date, @PathVariable String personID){
      return shiftService.callbackShift(shiftService.oneSchedule(date, personID), shiftService.isFound(), personID);
  }
    @GetMapping("/myworks/{date}/{personID}")
    public List<CallbackMyWorks> workList(@PathVariable String date, @PathVariable String personID){
        return shiftService.callbackWorksList(date, personID);
    }
    @GetMapping("/month/{date}/{personID}")
    public List<CallbackMyWorks> monthworkList(@PathVariable String date, @PathVariable String personID){
        return shiftService.callbackMonthList(date, personID);
    }
}
