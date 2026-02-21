package org.bit.bitgram.domain.auth.dto;
import org.bit.bitgram.domain.user.entity.AuthProvider;
import java.util.Map;

public record KakaoUserInfo(Map<String, Object> attributes) implements AuthUserInfo {
    @Override public String getProviderId() { return String.valueOf(attributes.get("id")); }
    @Override public AuthProvider getProvider() { return AuthProvider.KAKAO; }
    @Override public String getEmail() { 
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return account != null ? (String) account.get("email") : null;
    }
    @Override public String getNickname() { 
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }
    @Override public String getProfileImageUrl() { 
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("profile_image") : null;
    }
}