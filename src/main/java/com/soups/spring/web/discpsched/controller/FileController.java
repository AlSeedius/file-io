package com.soups.spring.web.discpsched.controller;

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @GetMapping("/year/{year}")
    public String addYear(@PathVariable String year) {
        fileService.addYear(Integer.parseInt(year));
        return "OK";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String addInfo="! ";
        fileService.uploadFile(file);
        if (fileService.changesInSchedule.isEmpty() & fileService.newMonths.isEmpty())
            addInfo=", но не было обнаружено никаких изменений по сравнению с предыдущим графиком.";
        else {
            if (!fileService.changesInSchedule.isEmpty()) {
                addInfo+= "\nВнесены изменения:\n ";
                for (List<String> list : fileService.changesInSchedule)
                    addInfo += "Месяц: " + list.get(0) + ", Диспетчер: " + list.get(1) + "; \n ";
                addInfo=addInfo.substring(0,addInfo.length()-2);
                addInfo+=". ";
            }
            if (!fileService.newMonths.isEmpty()){
                addInfo+="\nДобавлены месяцы:\n ";
                for (Integer months : fileService.newMonths) {
                    addInfo += months.toString() + ", ";
                }
                addInfo=addInfo.substring(0,addInfo.length()-2);
                addInfo+=".";
            }
            Integer nRDU = fileService.nRdu;
            String topic;
            if (nRDU==1)
                topic="VoronezhAll";
            else
                topic="LipeckAll";
            PushNotificationRequest request = new PushNotificationRequest("Внимание!", "Был загружен новый график дежурств. Проверьте ближайшие смены!", topic);
            sendNotification(request);
        }
            redirectAttributes.addFlashAttribute("message",
                    "Вы успешно загрузили файл " + file.getOriginalFilename() + addInfo);
        return "redirect:/";
    }

    public ResponseEntity sendNotification(PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);}

    @PostMapping("/uploadFiles")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes) {

        Arrays.asList(files)
            .stream()
            .forEach(file -> fileService.uploadFile(file));

        redirectAttributes.addFlashAttribute("message",
            "You successfully uploaded all files!");

        return "redirect:/";
    }
}
