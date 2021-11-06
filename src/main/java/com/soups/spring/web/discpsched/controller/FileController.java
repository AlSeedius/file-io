package com.soups.spring.web.discpsched.controller;

import java.util.Arrays;
import java.util.List;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.hms.HMSService;
import com.soups.spring.web.discpsched.model.PushNotificationRequest;
import com.soups.spring.web.discpsched.model.PushNotificationResponse;
import com.soups.spring.web.discpsched.service.FileService;
import com.soups.spring.web.discpsched.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    HMSService hmsService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @GetMapping("/changelog")
    public String index_() {
        return "changelog";
    }

    @GetMapping("/privacy")
    public String index__() {return "privacy";}

    @GetMapping("/year/{year}")
    public String addYear(@PathVariable String year) {
        fileService.addYear(Integer.parseInt(year));
        return "OK";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @RequestParam("rduType") String type) {
        String addInfo = "! ";
        int t = 0;
        if (type.equals("Воронежское РДУ"))
            t = 1;
        else if (type.equals("Липецкое РДУ"))
            t = 2;
        fileService.uploadFile(file, t);
        String[] rows = fileService.fileOutput.getOutput();
        if (fileService.fileOutput.type!=4){
            Rdu rdu = fileService.nRdu;
            String topic = rdu.getTopic();
            PushNotificationRequest request = new PushNotificationRequest("Внимание!", "Был загружен новый график дежурств. Проверьте ближайшие смены!", topic);
        //    hmsService.sendHMSTopicNotification("Был загружен новый график дежурств. Проверьте ближайшие смены!", "Внимание!", topic);
            //      sendNotification(request);
        }
        redirectAttributes.addFlashAttribute("message1",
                "Вы успешно загрузили файл " + file.getOriginalFilename());
        redirectAttributes.addFlashAttribute("message2", rows[0]);
        redirectAttributes.addFlashAttribute("message3", rows[1]);
        return "redirect:/";
    }

    public ResponseEntity sendNotification(PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);}

}