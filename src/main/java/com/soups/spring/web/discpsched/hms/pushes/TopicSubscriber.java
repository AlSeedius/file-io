package com.soups.spring.web.discpsched.hms.pushes;

import java.util.ArrayList;
import java.util.List;

public class TopicSubscriber {
    public String topic;
    public List<String> tokenArray;

    public TopicSubscriber(String topic, String token) {
        this.topic = topic;
        this.tokenArray = new ArrayList<String>();
        this.tokenArray.add(token);
    }
}
