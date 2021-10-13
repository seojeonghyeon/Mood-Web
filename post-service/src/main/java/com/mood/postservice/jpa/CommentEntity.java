package com.mood.postservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String commentId;

    @Column(nullable = false)
    private String commentUid;

    @Column(nullable = false)
    private String commentContents;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private int commentClass;

    @Column(nullable = false)
    private int commentGroup;

    @Column(nullable = false)
    private LocalDateTime commentTime;

    @Column(nullable = false)
    private int commentLikeCount;

    @Column(nullable = false)
    private boolean disabled;
}
