package com.mood.postservice.jpa;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String userUid;

    @Column(nullable = false)
    private String encryptedPwd;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String birthdate;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private String profileImageIcon;

    @Column(nullable = false)
    private String userGrade;

    @Column(nullable = false)
    private LocalDateTime gradeStart;

    @Column(nullable = true)
    private LocalDateTime gradeEnd;

    @Column(nullable = false)
    private int coin;

    @Column(nullable = false)
    private int ticket;

    @Column(nullable = false)
    private int loginCount;

    @Column(nullable = false)
    private LocalDateTime createTimeAt;

    @Column(nullable = false)
    private LocalDateTime recentLoginTime;

    @Column(nullable = false)
    private LocalDateTime matchingTime;

    @Column(nullable = false)
    private LocalDateTime nextMatchingTime;

    @Column(nullable = false)
    private boolean resetMatching;

    @Column(nullable = true)
    private String creditPwd;

    @Column(nullable = false)
    private boolean creditEnabled;

    @Column(nullable = true)
    private LocalDateTime creditTime;

    @Column(nullable = false)
    private boolean userLock;

    @Column(nullable = true)
    private int creditNumber;

    @Column(nullable = false)
    private boolean disabled;
}
