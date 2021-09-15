package com.mood.userservice.service;



public interface MessagingService {
    public int createRandomNumber();
    public void sendMessage(String message, String toNumber);
}
