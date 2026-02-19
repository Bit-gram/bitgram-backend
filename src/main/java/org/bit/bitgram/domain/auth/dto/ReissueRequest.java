package org.bit.bitgram.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequest {
    private String email;
    private String refreshToken;
}