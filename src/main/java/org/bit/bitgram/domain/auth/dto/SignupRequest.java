package org.bit.bitgram.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bit.bitgram.domain.user.entity.Role;
import org.bit.bitgram.domain.user.entity.User;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(Role.USER)
                .build();
    }
}