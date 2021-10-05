package com.mood.userservice.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rateplans")
public class RatePlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String rateplanId;

    @Column(nullable = false, length = 50)
    private String rateplanType;

    @Column(nullable = false, unique = true)
    private String productId;

    @Column(nullable = false, unique = true)
    private int months;

    @Column(nullable = false)
    private boolean disabled;
}
