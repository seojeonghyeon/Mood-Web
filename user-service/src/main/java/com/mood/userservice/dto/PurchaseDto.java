package com.mood.userservice.dto;

import lombok.Data;

@Data
public class PurchaseDto {
    private String userUid;

    private String orderId;

    private String packageName;

    private String productId;

    private String purchaseTime;

    private int purchaseState;

    private String purchaseToken;

    private boolean acknowledged;
}
