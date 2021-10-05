package com.mood.userservice.vo;

import lombok.Data;

@Data
public class RequestPurchase {
    private String orderId;

    private String packageName;

    private String productId;

    private String purchaseTime;

    private int purchaseState;

    private String purchaseToken;

    private boolean acknowledged;
}
