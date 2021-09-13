package com.mood.matchingservice.controller;

import com.mood.matchingservice.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matching-service")
public class MatchingController {
    private Environment env;
    private MatchingService matchingService;

    @Autowired
    public MatchingController(Environment env, MatchingService matchingService){
        this.env=env;
        this.matchingService = matchingService;
    }

    //Back-end Server, User Service Health Check
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service"
                +", port(local.server.port)=" + env.getProperty("local.server.port")
                +", port(server.port)=" + env.getProperty("server.port")
                +", token secret=" + env.getProperty("token.secret")
                +", token expiration time=" + env.getProperty("token.expiration_time"));
    }
}
