package org.bit.bitgram.domain.auth.dto;
import org.bit.bitgram.domain.user.entity.AuthProvider;

public interface AuthUserInfo {
    String getProviderId();
    AuthProvider getProvider();
    String getEmail();
    String getNickname();
    String getProfileImageUrl();
}