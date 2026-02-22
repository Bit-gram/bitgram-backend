package org.bit.bitgram.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserInfo { 
    private String email;
    private String nickname;
    private String profileImageUrl;
}