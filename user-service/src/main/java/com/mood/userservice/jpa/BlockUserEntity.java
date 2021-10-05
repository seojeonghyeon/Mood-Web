package com.mood.userservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blockusers")
public class BlockUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String blockUid;

    @Column(nullable = false)
    private String userUid;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private LocalDateTime blockTime;

    @Column(nullable = false)
    private boolean disabled;
}
