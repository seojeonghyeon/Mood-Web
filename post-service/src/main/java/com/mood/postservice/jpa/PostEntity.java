package com.mood.postservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String postId;

    @Column(nullable = false)
    private String postUid;

    @Column(nullable = false)
    private String postImage;

    @Column(nullable = false)
    private String locationENG;

    @Column(nullable = false)
    private String locationKOR;

    @Column(nullable = false)
    private String postContents;

    @Column(nullable = false)
    private int postLikeCount;

    @Column(nullable = false)
    private int postCommentCount;

    @Column(nullable = false)
    private LocalDateTime postTime;

    @Column(nullable = false)
    private boolean disabled;
}
