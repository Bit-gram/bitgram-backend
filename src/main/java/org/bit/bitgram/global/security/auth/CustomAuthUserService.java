package org.bit.bitgram.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.auth.dto.GoogleUserInfo; 
import org.bit.bitgram.domain.auth.dto.KakaoUserInfo;
import org.bit.bitgram.domain.auth.dto.OAuth2UserInfo;
import org.bit.bitgram.domain.user.entity.User;
import org.bit.bitgram.domain.user.entity.Role;
import org.bit.bitgram.domain.user.repository.UserRepository;
import org.bit.bitgram.global.security.user.CustomUserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        OAuth2UserInfo userInfo = switch (registrationId) {
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserInfo(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        };

        User user = saveOrUpdate(userInfo);

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuth2UserInfo userInfo) { 
        return userRepository.findByEmailAndProvider(userInfo.getEmail(), userInfo.getProvider())
                .map(user -> user)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .nickname(userInfo.getNickname())
                        .profileImageUrl(userInfo.getProfileImageUrl())
                        .role(Role.USER)
                        .provider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .build()));
    }
}