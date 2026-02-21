package org.bit.bitgram.domain.auth.dto;
import org.bit.bitgram.domain.user.entity.AuthProvider;
import java.util.Map;

public record GoogleUserInfo(Map<String, Object> attributes) implements AuthUserInfo {
    @Override public String getProviderId() { return (String) attributes.get("sub"); }
    @Override public AuthProvider getProvider() { return AuthProvider.GOOGLE; }
    @Override public String getEmail() { return (String) attributes.get("email"); }
    @Override public String getNickname() { return (String) attributes.get("name"); }
    @Override public String getProfileImageUrl() { return (String) attributes.get("picture"); }
}