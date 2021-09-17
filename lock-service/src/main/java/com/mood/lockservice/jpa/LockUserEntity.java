package com.mood.lockservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lockusers")
public class LockUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String lockUid;

    @Column(nullable = false)
    private String lockUserUid;

    @Column(nullable = false)
    private String lockType;

    @Column(nullable = false, length = 300)
    private String lockReasons;

    @Column(nullable = false)
    private String referUid;

    @Column(nullable = false)
    private boolean lockUserDisabled;

    @Column(nullable = false)
    private LocalDateTime activeTime;
}
