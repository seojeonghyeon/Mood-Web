package com.mood.userservice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestPurchase {
    private String userUid;
    private LocalDateTime purchaseTime;
    private String purchaseContents;
}
