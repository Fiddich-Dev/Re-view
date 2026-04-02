package com.fiddich.review.user;

import com.fiddich.review.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password; // 소셜 로그인 유저는 null

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    private String providerId; // 소셜 로그인 제공자의 고유 ID

    @Builder
    private User(String email, String password, String name, Platform platform,
                 AuthProvider authProvider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.platform = platform;
        this.authProvider = authProvider;
        this.providerId = providerId;
    }
}
