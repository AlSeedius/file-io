package com.soups.spring.web.discpsched.service;

import com.soups.spring.web.discpsched.DAO.*;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.entitie.Schedule;
import com.soups.spring.web.discpsched.entitie.User;
import com.soups.spring.web.discpsched.firebase.FCMService;
import com.soups.spring.web.discpsched.hms.HMSService;
import com.soups.spring.web.discpsched.model.PushIDRequest;
import com.soups.spring.web.discpsched.model.PushNotificationRequest;
import com.soups.spring.web.discpsched.model.UpdateTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @Autowired
    RduRepository rduRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    HMSService hmsService;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @Scheduled(cron = "0 00 20 * * *" )
    public void sendReminder() {
        List<Schedule> schedules = scheduleRepository.findByDateId(calendarRepository.findByDay(LocalDate.now().plusDays(2)).getId());
        String authToken = hmsService.getHMSAccessToken().getAccess_token();
        for (Schedule schedule : schedules) {
            if (userRepository.findByAppID(schedule.getPersonId()).size() > 0) {
                for (User user : userRepository.findByAppID(schedule.getPersonId())) {
                    PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
                    pushNotificationRequest.setTitle("Не забудьте!");
                    String token = user.getToken();
                    try {
                        if (token.length() > 0) {
                            if (schedule.getType().equals("1")) {
                                if (user.getDeviceType()==1) {
                                    String message = "У Вас завтра дневная смена. Выспитесь крепко!";
                                    if (schedule.getCurrentPlace()!=0)
                                        message+= " Работать нужно не где обычно, а на " + schedule.getCurrentPlace() + "-м рабочем месте";
                                    pushNotificationRequest.setMessage(message);
                                    pushNotificationRequest.setToken(token);
                                    fcmService.sendMessageToToken(pushNotificationRequest);
                                }
                                else if (user.getDeviceType()==2){
                                    hmsService.sendHMSTokenNotification("У Вас завтра дневная смена. Выспитесь крепко!", "Не забудьте!", user.getToken());
                                }
                            } else if (schedule.getType().equals("7") | schedule.getType().equals("8") | schedule.getType().equals("4")) {
                                if (user.getDeviceType()==1) {
                                pushNotificationRequest.setMessage("У Вас завтра работа в качестве специалиста");
                                pushNotificationRequest.setToken(token);
                                fcmService.sendMessageToToken(pushNotificationRequest);                                }
                                else if (user.getDeviceType()==2){
                                    hmsService.sendHMSTokenNotification("У Вас завтра работа в качестве специалиста", "Не забудьте!", user.getToken());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Throwable cause = e.getCause();
                        if (cause.getMessage().equals("NOT_FOUND") || cause.getMessage().equals("UNREGISTERED") || cause.getMessage().equals("Requested entity was not found.")) {
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

    @Scheduled (cron = "0 00 0 * * *")
    public void sendVacationReminder() {
        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
        boolean notificationNeededToBeSent;
        List<Schedule> schedules = scheduleRepository.findByDateId(calendarRepository.findByDay(LocalDate.now().plusDays(2)).getId());
        for (Schedule s : schedules) {
            notificationNeededToBeSent = false;
            if (s.getType().equals("О")) {
                if (scheduleRepository.findByDateIdAndPersonId(s.getDateId() - 1, s.getPersonId()).isEmpty()) {
                    notificationNeededToBeSent = true;
                } else {
                    if (!scheduleRepository.findByDateIdAndPersonId(s.getDateId() - 1, s.getPersonId()).get(0).getType().equals("О"))
                        notificationNeededToBeSent = true;
                }
                if (notificationNeededToBeSent) {
                    List<User> users = userRepository.findByAppID(s.getPersonId());
                    String title = "Принимайте поздравления!";
                    String message = "Пусть Ваш отпуск будет незабываемым и дарит только позитивные эмоции!";
                    for (User user : users) {
                        try {
                            if (user.getDeviceType() == 1) {
                                pushNotificationRequest = new PushNotificationRequest();
                                pushNotificationRequest.setTitle(title);
                                pushNotificationRequest.setMessage(message);
                                pushNotificationRequest.setToken(user.getToken());
                                fcmService.sendMessageToToken(pushNotificationRequest);
                            } else if (user.getDeviceType() == 2) {
                                hmsService.sendHMSTokenNotification(message, title, user.getToken());
                            }
                        } catch (Exception e) {
                            Throwable cause = e.getCause();
                            if (cause.getMessage().equals("NOT_FOUND") || cause.getMessage().equals("UNREGISTERED") || cause.getMessage().equals("Requested entity was not found.")) {
                                User u = userRepository.findByToken(pushNotificationRequest.getToken());
                                if (u != null) {
                                    userRepository.delete(u);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkIfTokenExists(PushIDRequest request) {
        User u = userRepository.findByToken(request.getToken());
        if (u == null)
            uploadId(request);
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

    private List<String> allTopics(){
        List<String> result = new ArrayList<>();
        for (Rdu r : rduRepository.findAll())
            result.add(r.getTopic());
        return result;
    }

    public void uploadId(PushIDRequest request){
        try{
            User user = userRepository.findByToken(request.getToken());
            Integer deviceType = request.getDeviceType();
            if (deviceType == null)
                deviceType = 1;
            if (user == null) {
                user = new User(request.getToken(), request.getUserId(), deviceType);
                userRepository.save(user);
                if (deviceType == 1)
                    uploadFCM(user, request);
                else if (deviceType == 2)
                    hmsService.uploadHMS(user, request);
            }
            else if (request.getUserId()!=user.getAppID()) {
                if (deviceType == 1)
                    uploadFCM(user, request);
                else if (deviceType == 2)
                    hmsService.uploadHMS(user, request);
                user.setAppID(request.getUserId());
                userRepository.save(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateToken(UpdateTokenRequest request) {
        if (userRepository.findByToken(request.getOldToken()) != null) {
            User user = userRepository.findByToken(request.getOldToken());
            user.setToken(request.getNewToken());
            userRepository.save(user);
            if (user.getDeviceType() == 1)
                updateFCM(user, request);
            else if (user.getDeviceType() == 2)
                hmsService.updateHMS(user, request);
        }
    }

    private void uploadFCM(User user, PushIDRequest request) {
        try {
            for (String s : allTopics())
                fcmService.unsubscribeUsers(s, request.getToken());
            fcmService.subscribeUsers("All", request.getToken());
            fcmService.subscribeUsers(request.getTopic(), request.getToken());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    private void updateFCM(User user, UpdateTokenRequest request) {
        try {
            for (String s : allTopics())
                fcmService.unsubscribeUsers(s, request.getOldToken());
            fcmService.subscribeUsers("All", request.getNewToken());
            fcmService.subscribeUsers(rduRepository.findById(personRepository.findById(user.getAppID()).get().getRduId()).get().getTopic(), request.getNewToken());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    public void testUser(Integer userId) {
        for (User u : userRepository.findByAppID(userId)) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
            try {
                String title = "Тест";
                String body = "Тестовое сообщение";
                if (u.getDeviceType() == 1){
                pushNotificationRequest.setTitle(title);
                pushNotificationRequest.setMessage(body);
                pushNotificationRequest.setToken(u.getToken());
                fcmService.sendMessageToToken(pushNotificationRequest);}
                else if (u.getDeviceType() == 2){
                    hmsService.sendHMSTokenNotification(body, title, u.getToken());
                }
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause.getMessage().equals("NOT_FOUND") || cause.getMessage().equals("UNREGISTERED")) {
                    User user = userRepository.findByToken(pushNotificationRequest.getToken());
                    if (user != null) {
                        userRepository.delete(user);
                    }
                }
                logger.error(e.getMessage());
            }
        }
    }

    public void uploadBoss(PushIDRequest request) {
        try {
            User user = userRepository.findByToken(request.getToken());
            if (user != null) {
                userRepository.delete(user);
            }
            Integer deviceType=1;
            if (user!=null){
                if (user.getDeviceType()==2)
                    deviceType=2;
            }
            if (deviceType ==1){
            for (String s : allTopics())
                fcmService.unsubscribeUsers(s, request.getToken());
            fcmService.subscribeUsers("All", request.getToken());
            fcmService.subscribeUsers(request.getTopic(), request.getToken());}
            else if (deviceType == 2) {
                String authToken = hmsService.getHMSAccessToken().getAccess_token();
                for (String s : allTopics())
                    hmsService.unSubscribeHMSUser(s, request.getToken(), authToken);
                hmsService.subscribeHMSUser("All", request.getToken(), authToken);
                hmsService.subscribeHMSUser(request.getTopic(), request.getToken(), authToken);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
