package com.mood.postservice.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="usergrades")
public class UserGradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String gradeUid;

    @Column(nullable = false)
    private String gradeType;

    @Column(nullable = false)
    private String gradePercent;

    @Column(nullable = false)
    private int gradeDate;

    @Column(nullable = false)
    private boolean disabled;
}
