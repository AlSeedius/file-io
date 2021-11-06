package com.soups.spring.web.discpsched.controller;

import com.soups.spring.web.discpsched.model.PushIDRequest;
import com.soups.spring.web.discpsched.model.PushNotificationRequest;
import com.soups.spring.web.discpsched.model.PushNotificationResponse;
import com.soups.spring.web.discpsched.model.UpdateTokenRequest;
import com.soups.spring.web.discpsched.service.PushNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PushNotificationController {

    private PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/notification/topic")
    public ResponseEntity sendNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/token")
    public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/upload")
    public ResponseEntity uploadID(@RequestBody PushIDRequest request) {
        pushNotificationService.uploadId(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "User uploaded"), HttpStatus.OK);
    }
    @PostMapping("/notification/uploadBoss")
    public ResponseEntity uploadBoss(@RequestBody PushIDRequest request) {
        pushNotificationService.uploadBoss(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "User uploaded"), HttpStatus.OK);
    }
    @GetMapping("/notification/test")
    public ResponseEntity test(@RequestBody PushNotificationRequest request){
        pushNotificationService.sendReminder();
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "OK"), HttpStatus.OK);
    }

    @GetMapping("/notification/usertest/{userId}")
    public ResponseEntity usertest(@PathVariable Integer userId){
        pushNotificationService.testUser(userId);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "OK"), HttpStatus.OK);
    }

    @PostMapping("/notification/update")
    public ResponseEntity test(@RequestBody UpdateTokenRequest request){
        pushNotificationService.updateToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "OK"), HttpStatus.OK);
    }
}
