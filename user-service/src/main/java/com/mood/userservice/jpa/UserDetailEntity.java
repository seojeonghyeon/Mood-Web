package com.mood.userservice.jpa;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "userdetails")
public class UserDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userUid;

    @Column(nullable = false, unique = true)
    private String phoneNum;

    @Column(nullable = false)
    private boolean gender;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean otherM;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean otherW;

    @Column(nullable = false)
    private double respect;

    @Column(nullable = false)
    private double contact;

    @Column(nullable = false)
    private double date;

    @Column(nullable = false)
    private double communication;

    @Column(nullable = false)
    private double sex;

    @Column(nullable = false, length = 300)
    private String work;

    @Column(nullable = false, length = 300)
    private String happy;

    @Column(nullable = false, length = 300)
    private String dating;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = true)
    private String subLocation;

    @Column(nullable = true)
    private double subLatitude;

    @Column(nullable = true)
    private double subLongitude;

    @Column(nullable = false)
    @ColumnDefault("newbie")
    private String userGrade;

    @Column(nullable = false)
    private LocalDateTime gradeStart;

    @Column(nullable = true)
    private LocalDateTime gradeEnd;

    @Column(nullable = false)
    private int userAge;

    @Column(nullable = false)
    private int minAge;

    @Column(nullable = false)
    private int maxAge;

    @Column(nullable = false)
    private int maxDistance;

    @Column(nullable = true)
    private int userGroup;

    @Column(nullable = false)
    private LocalDateTime recentLoginTime;

    @Column(nullable = false)
    private boolean disabled;
}
