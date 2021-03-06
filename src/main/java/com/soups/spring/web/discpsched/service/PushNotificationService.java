package com.soups.spring.web.discpsched.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.DAO.UserRepository;
import com.soups.spring.web.discpsched.entitie.Schedule;
import com.soups.spring.web.discpsched.entitie.User;
import com.soups.spring.web.discpsched.firebase.FCMService;
import com.soups.spring.web.discpsched.model.PushIDRequest;
import com.soups.spring.web.discpsched.model.PushNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    CalendarRepository calendarRepository;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @Scheduled(cron = "0 00 20 * * *" )
    public void sendReminder() {
        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
        pushNotificationRequest.setTitle("Не забудьте!");
        List<Schedule> schedules = scheduleRepository.findByDateId(calendarRepository.findByDay(LocalDate.now().plusDays(2)).getId());
        for (Schedule schedule : schedules) {
            if (userRepository.findByAppID(schedule.getPersonId()).size() > 0) {
                for (User user : userRepository.findByAppID(schedule.getPersonId())) {
                    String token = user.getToken();
                    try {
                        if (token.length() > 0) {
                            if (schedule.getType().equals("1")) {
                                pushNotificationRequest.setMessage("У Вас завтра дневная смена. Выспитесь крепко!");
                                pushNotificationRequest.setToken(token);
                                fcmService.sendMessageToToken(pushNotificationRequest);
                                //      logger.info("Отправили на" + token);
                            } else if (schedule.getType().equals("7") | schedule.getType().equals("8") | schedule.getType().equals("4")) {
                                pushNotificationRequest.setMessage("У Вас завтра работа в качестве специалиста");
                                pushNotificationRequest.setToken(token);
                                fcmService.sendMessageToToken(pushNotificationRequest);
                                //      logger.info("Отправили на" + token);
                            }
                        }
                    } catch (Exception e) {
                        Throwable cause = e.getCause();
                        if (cause.getMessage().equals("NOT_FOUND") || cause.getMessage().equals("UNREGISTERED")) {
                            User u = userRepository.findByToken(pushNotificationRequest.getToken());
                            if (u != null) {
                                userRepository.delete(u);
                            }
                        }
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    public void sendPushNotificationWithoutData(PushNotificationRequest request) {
        try {
            fcmService.sendMessageWithoutData(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void uploadId(PushIDRequest request){
        try{
            User user = userRepository.findByToken(request.getToken());
            if (user == null)
            {
                user = new User(request.getToken(), request.getUserId());
                userRepository.save(user);
         //       fcmService.unsubscribeUsers("VoronezhAll", request.getToken());
         //       fcmService.unsubscribeUsers("LipeckAll", request.getToken());
                fcmService.subscribeUsers("All", request.getToken());
                fcmService.subscribeUsers(request.getTopic(), request.getToken());
            }
            else if (request.getUserId()!=user.getAppID()){
          //      fcmService.unsubscribeUsers("VoronezhAll", request.getToken());
          //      fcmService.unsubscribeUsers("LipeckAll", request.getToken());
                fcmService.subscribeUsers(request.getTopic(), request.getToken());
                user.setAppID(request.getUserId());
                userRepository.save(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    public void uploadBoss(PushIDRequest request) {
        try {
            User user = userRepository.findByToken(request.getToken());
            if (user != null) {
                userRepository.delete(user);
            }
            fcmService.unsubscribeUsers("VoronezhAll", request.getToken());
            fcmService.unsubscribeUsers("LipeckAll", request.getToken());
            fcmService.subscribeUsers("All", request.getToken());
            fcmService.subscribeUsers(request.getTopic(), request.getToken());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


  /*  private Map<String, String> getSamplePayloadData() {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", defaults.get("payloadMessageId"));
        pushData.put("text", defaults.get("payloadData") + " " + LocalDateTime.now());
        return pushData;
    }


    private PushNotificationRequest getSamplePushNotificationRequest() {
        PushNotificationRequest request = new PushNotificationRequest(defaults.get("title"),
                defaults.get("message"),
                defaults.get("topic"));
        return request;
    }*/


}
