package org.bit.bitgram.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.auth.dto.LoginRequest;
import org.bit.bitgram.domain.auth.dto.ReissueRequest;
import org.bit.bitgram.domain.auth.dto.SignupRequest;
import org.bit.bitgram.domain.auth.dto.TokenResponse;
import org.bit.bitgram.domain.auth.service.AuthService;
import org.bit.bitgram.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success());
    }
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestBody ReissueRequest request) {
        TokenResponse tokenResponse = authService.reissue(request.getEmail(), request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}