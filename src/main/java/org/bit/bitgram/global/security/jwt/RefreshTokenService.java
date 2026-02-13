package org.bit.bitgram.global.security.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(
            "RT:" + email, 
            refreshToken, 
            Duration.ofDays(7) 
        );
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        String savedToken = redisTemplate.opsForValue().get("RT:" + email);
        return refreshToken.equals(savedToken);
    }
    
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:" + email);
    }
}