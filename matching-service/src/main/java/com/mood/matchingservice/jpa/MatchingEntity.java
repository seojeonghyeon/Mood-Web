package com.mood.matchingservice.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "matchings")
public class MatchingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
