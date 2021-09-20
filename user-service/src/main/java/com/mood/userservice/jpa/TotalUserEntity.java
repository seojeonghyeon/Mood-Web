package com.mood.userservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "totaluser")
public class TotalUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean disabled;

    @Column(nullable = false)
    private int totaluser;

    @Column(nullable = false)
    private LocalDateTime createAt;
}
