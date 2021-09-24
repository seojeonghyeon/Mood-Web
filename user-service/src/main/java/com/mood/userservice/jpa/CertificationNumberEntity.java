package com.mood.userservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "totalusers")
public class CertificationNumberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean disabled;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private int creditNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
