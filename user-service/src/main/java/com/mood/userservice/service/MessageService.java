package com.mood.userservice.service;

import java.util.Random;

public interface MessageService {
    int createRandomNumber();
    void sendMessage(String message, String toNumber);
}
