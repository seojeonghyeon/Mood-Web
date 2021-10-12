package com.mood.postservice.jpa;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hashtags")
public class HashtagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hashTagId;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String postUid;

    @Column(nullable = false)
    private String hashtagName;

    @Column(nullable = false)
    private LocalDateTime hashingTime;

    @Column(nullable = false)
    private boolean disabled;
}
