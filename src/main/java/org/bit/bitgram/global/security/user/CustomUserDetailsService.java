package org.bit.bitgram.global.security.user;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.user.entity.User;
import org.bit.bitgram.domain.user.repository.UserRepository;
import org.bit.bitgram.global.common.enums.ErrorCode;
import org.bit.bitgram.global.exception.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}