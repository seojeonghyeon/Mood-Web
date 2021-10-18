package com.mood.matchingservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matchings")
public class MatchingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String matchingId;

    @Column(nullable = false)
    private String userUid;

    @Column(nullable = false)
    private String otherUserUid;

    @Column(nullable = false)
    private LocalDateTime matchingTime;

    @Column(nullable = false)
    private double moodDistance;

    @Column(nullable = false)
    private boolean disabled;
}
