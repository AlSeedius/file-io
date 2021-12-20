package com.soups.spring.web.discpsched.hms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.RduRepository;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.entitie.User;
import com.soups.spring.web.discpsched.hms.pushes.TokenRootNotification;
import com.soups.spring.web.discpsched.hms.pushes.TopicRootNotification;
import com.soups.spring.web.discpsched.hms.pushes.TopicSubscriber;
import com.soups.spring.web.discpsched.model.PushIDRequest;
import com.soups.spring.web.discpsched.model.UpdateTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HMSService {

    private String appId = "104864327";
    private String secret = "a3ccdcf4914bb699f97282eca68517059a75920682d9347620df4146a5ceca6b";

    @Autowired
    RduRepository rduRepository;

    @Autowired
    PersonRepository personRepository;

    public Token getHMSAccessToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("grant_type", "client_credentials");
            map.add("client_id", appId);
            map.add("client_secret", secret);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(restTemplate.postForEntity(url, request, String.class).getBody(), Token.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void sendHMSTokenNotification(String body, String header, List<String> tokens) throws IOException {
        //public ResponseEntity sendHMSTokenNotification(String body, String header, List<String> tokens) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/messages:send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(getHMSAccessToken().getAccess_token());
        TokenRootNotification notification = new TokenRootNotification(body, header, tokens);
        String json = objectMapper.writeValueAsString(notification);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
    public void sendHMSTokenNotification(String body, String header, String token) throws IOException {
        //public ResponseEntity sendHMSTokenNotification(String body, String header, List<String> tokens) throws IOException{
        List<String> tokens = new ArrayList<String>();
        tokens.add(token);
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/messages:send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(getHMSAccessToken().getAccess_token());
        TokenRootNotification notification = new TokenRootNotification(body, header, tokens);
        String json = objectMapper.writeValueAsString(notification);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
    public void sendHMSTokenNotification(String body, String header, List<String> tokens, String authToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/messages:send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(authToken);
        TokenRootNotification notification = new TokenRootNotification(body, header, tokens);
        String json = objectMapper.writeValueAsString(notification);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    public void sendHMSTopicNotification(String body, String header, String topic) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://push-api.cloud.huawei.com/v1/" + appId + "/messages:send";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.setBearerAuth(getHMSAccessToken().getAccess_token());
            TopicRootNotification notification = new TopicRootNotification(body, header, topic);
            String json = objectMapper.writeValueAsString(notification);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
        }
    }

    public void sendHMSTopicNotification(String body, String header, String topic, String authToken) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://push-api.cloud.huawei.com/v1/" + appId + "/messages:send";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.setBearerAuth(authToken);
            TopicRootNotification notification = new TopicRootNotification(body, header, topic);
            String json = objectMapper.writeValueAsString(notification);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
        }
    }

    public void subscribeHMSUser(String topic, String token, String authToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/topic:subscribe";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        subscribeHelperWithAuthToken(topic, token, authToken, objectMapper, restTemplate, url, headers);
    }

    public void subscribeHMSUser(String topic, String token) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/topic:subscribe";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        subscribeHelperGetAuthToken(topic, token, objectMapper, restTemplate, url, headers);
    }

    public void unSubscribeHMSUser(String topic, String token) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/topic:unsubscribe";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        subscribeHelperGetAuthToken(topic, token, objectMapper, restTemplate, url, headers);
    }


    public void unSubscribeHMSUser(String topic, String token, String authToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://push-api.cloud.huawei.com/v1/"+appId+"/topic:unsubscribe";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        subscribeHelperWithAuthToken(topic, token, authToken, objectMapper, restTemplate, url, headers);
    }

    private void subscribeHelperWithAuthToken(String topic, String token, String authToken, ObjectMapper objectMapper, RestTemplate restTemplate, String url, HttpHeaders headers) throws JsonProcessingException {
        headers.setBearerAuth(authToken);
        TopicSubscriber subscriber = new TopicSubscriber(topic, token);
        String json = objectMapper.writeValueAsString(subscriber);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    private void subscribeHelperGetAuthToken(String topic, String token, ObjectMapper objectMapper, RestTemplate restTemplate, String url, HttpHeaders headers) throws IOException {
        headers.setBearerAuth(getHMSAccessToken().getAccess_token());
        TopicSubscriber subscriber = new TopicSubscriber(topic, token);
        String json = objectMapper.writeValueAsString(subscriber);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    public void uploadHMS(User user, PushIDRequest request) throws IOException {
        String authToken = getHMSAccessToken().getAccess_token();
        for (String s : allTopics())
            unSubscribeHMSUser(s, request.getToken(), authToken);
        subscribeHMSUser("All", request.getToken(), authToken);
        subscribeHMSUser(request.getTopic(), request.getToken(), authToken);
    }

    public void updateHMS(User user, UpdateTokenRequest request) {
        try {
            for (String s : allTopics())
                unSubscribeHMSUser(s, request.getOldToken());
            subscribeHMSUser("All", request.getNewToken());
            subscribeHMSUser(rduRepository.findById(personRepository.findById(user.getAppID()).get().getRduId()).get().getTopic(), request.getNewToken());
        } catch (Exception e) {
        }
    }

    private List<String> allTopics(){
        List<String> result = new ArrayList<>();
        for (Rdu r : rduRepository.findAll())
            result.add(r.getTopic());
        return result;
    }
}
