package com.mood.userservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certifications")
public class CertificationNumberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String certificationUid;

    @Column(nullable = false)
    private boolean disabled;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private int creditNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
