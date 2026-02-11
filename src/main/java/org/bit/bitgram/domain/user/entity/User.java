package org.bit.bitgram.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.bit.bitgram.global.common.ModifiedTimeEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends ModifiedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 소셜 로그인 관련
    private String provider;   // google, kakao, naver 등
    private String providerId; // 소셜 서비스에서의 고유 식별값
}