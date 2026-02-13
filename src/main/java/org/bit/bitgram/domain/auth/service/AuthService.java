package org.bit.bitgram.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.auth.dto.LoginRequest;
import org.bit.bitgram.domain.auth.dto.SignupRequest;
import org.bit.bitgram.domain.auth.dto.TokenResponse;
import org.bit.bitgram.global.security.jwt.RefreshTokenService;
import org.bit.bitgram.global.security.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final org.bit.bitgram.domain.user.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = "RT-" + java.util.UUID.randomUUID();

        refreshTokenService.saveRefreshToken(authentication.getName(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    @Transactional
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new org.bit.bitgram.global.exception.BusinessException(org.bit.bitgram.global.common.enums.ErrorCode.USER_EMAIL_DUPLICATE);
        }
        
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        userRepository.save(signupRequest.toEntity(encodedPassword));
    }
    
    @Transactional
    public void logout(String email) {
        refreshTokenService.deleteRefreshToken(email);
    }
}