package com.mood.userservice.service;

import com.mood.userservice.jpa.UserDetailRepository;
import com.mood.userservice.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

@Service
@Slf4j
public class MessagingServiceImpl implements MessagingService{
    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;

    public int createRandomNumber(){
        Random random = new Random();
        int number = (random.nextInt(8888)+1111);
        return number;
    }

    public void sendMessage(String message, String toNumber){
        //Confirm the function, Environment wtf..
        log.info("Before send message : "+ LocalDateTime.now()+" = To : "+toNumber+" Message : "+message);

        String apiKey=env.getProperty("messaging.apiKey");
        String apiSecret=env.getProperty("messaging.apiSecret");
        String fromNumber=env.getProperty("messaging.fromNumber");
        log.info("Before send message : "+LocalDateTime.now()+" = From : "+fromNumber+" apiKey : "+apiKey+" apiSecret : "+apiSecret);

        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", toNumber);
        params.put("from", fromNumber);
        params.put("type", "SMS");
        params.put("text", message);
        params.put("app_version", "test app 0.5"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        log.info("After send message");
    }
}
