package com.mood.postservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "likeds")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String likeId;

    @Column(nullable = false)
    private String likeUid;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = true)
    private String commentId;

    @Column(nullable = false)
    private LocalDateTime likeTime;

    @Column(nullable = false)
    private boolean disabled;

}
