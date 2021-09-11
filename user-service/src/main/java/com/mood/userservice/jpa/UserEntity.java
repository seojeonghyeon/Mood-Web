package com.mood.userservice.jpa;

import com.mood.userservice.vo.ResponseMatchingUser;
import com.mood.userservice.vo.ResponsePost;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false, unique = true)
    private String encryptedPwd;

    @Column(nullable = false, unique = true)
    private String phoneNum;

    @Column(nullable = false)
    private String birthdate;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private String profileImageIcon;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int coin;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int ticket;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int loginCount;

    @Column(nullable = false)
    private LocalDateTime createTimeAt;

    @Column(nullable = false)
    private LocalDateTime recentLoginTime;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean resetMatching;

    @Column(nullable = true)
    private String creditPwd;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean userLock;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean disabled;
}
