package com.mood.userservice.jpa;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "purchases")
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String purchaseUId;

    @Column(nullable = false)
    private String userUid;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String packageName;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String purchaseTime;

    @Column(nullable = false)
    private int purchaseState;

    @Column(nullable = false)
    private String purchaseToken;

    @Column(nullable = false)
    private boolean acknowledged;
}
