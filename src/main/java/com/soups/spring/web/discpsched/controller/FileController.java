package com.soups.spring.web.discpsched.controller;

import com.soups.spring.web.discpsched.DAO.RduRepository;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.hms.HMSService;
import com.soups.spring.web.discpsched.model.PushNotificationRequest;
import com.soups.spring.web.discpsched.model.PushNotificationResponse;
import com.soups.spring.web.discpsched.service.FileService;
import com.soups.spring.web.discpsched.service.PushNotificationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    RduRepository rduRepository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    HMSService hmsService;

    @GetMapping("/")
    public String index(Model model) {
        List<String> rList = new ArrayList<>();
        for (Rdu r: rduRepository.findAll()) {
            if (r.getRsp()==1)
                rList.add(r.getName());
        }
        model.addAttribute("cdata", rList);
        return "upload";
    }

    @GetMapping("/newService")
    public String newRDU() {
        return "newRDU";
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
        int rduId = 0;
        if (!type.contains("Выберите"))
            rduId = rduRepository.findByName(type).getId();
        fileService.uploadFile(file, rduId);
        String[] rows = fileService.fileOutput.getOutput();
        if (!rows[0].equals("Не обнаружено изменений по сравнению с существующим графиком.")) {
            Rdu rdu = fileService.nRdu;
            String notificationHeader = "Внимание!";
            String notificationText;
            if (fileService.fileOutput.type == 4) {
                notificationText = "Был загружен новый график выхода на работу. Проверьте расписание в приложении!";
            } else {
                notificationText = "Был загружен новый график дежурств. Проверьте ближайшие смены в приложении!";
            }
            PushNotificationRequest request = new PushNotificationRequest(notificationHeader, notificationText, rdu.getTopic());
            hmsService.sendHMSTopicNotification(notificationText, notificationHeader, rdu.getTopic());
            sendNotification(request);
        }
        redirectAttributes.addFlashAttribute("message1",
                "Вы успешно загрузили файл " + file.getOriginalFilename());
        redirectAttributes.addFlashAttribute("message2", rows[0]);
        redirectAttributes.addFlashAttribute("message3", rows[1]);
        return "redirect:/";
    }

    @PostMapping("/addService")
    private String addService(@RequestParam("name") String name, @RequestParam("topic") String topic,
                             @RequestParam("type") Integer type, @RequestParam("rsp") Integer rsp, RedirectAttributes attributes){
        Rdu tempRdu = new Rdu();
        tempRdu.setName(name);
        tempRdu.setRsp(rsp);
        tempRdu.setTopic(topic);
        tempRdu.setType(type);
        rduRepository.save(tempRdu);
        attributes.addFlashAttribute("message", "Успешно добавлена служба: " + tempRdu.getName());
        return "redirect:/newService";
    }

    public ResponseEntity sendNotification(PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);}

}