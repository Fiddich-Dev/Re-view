package com.fiddich.review.user;

import com.fiddich.review.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    public static final String ERR_USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    public static final String ERR_DUPLICATE_EMAIL = "이미 사용 중인 이메일입니다.";

    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ERR_USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ERR_USER_NOT_FOUND));
    }

    @Transactional
    public User register(String email, String password, String name, Platform platform, AuthProvider authProvider) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ERR_DUPLICATE_EMAIL);
        }
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .name(name)
                .platform(platform)
                .authProvider(authProvider)
                .build());
    }

    @Transactional
    public User findOrCreateByOAuth(AuthProvider provider, String providerId, String email, String name) {
        return userRepository.findByProviderIdAndAuthProvider(providerId, provider)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .platform(Platform.APP)
                        .authProvider(provider)
                        .providerId(providerId)
                        .build()));
    }
}
