package com.fiddich.review.auth;

import com.fiddich.review.common.exception.BusinessException;
import com.fiddich.review.redis.RefreshTokenRedisRepository;
import com.fiddich.review.user.AuthProvider;
import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String ERR_INVALID_PASSWORD = "비밀번호가 올바르지 않습니다.";
    public static final String ERR_SOCIAL_LOGIN_REQUIRED = "소셜 로그인으로 가입된 계정입니다.";
    public static final String ERR_INVALID_REFRESH_TOKEN = "유효하지 않은 리프레시 토큰입니다.";

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public TokenResponse login(String email, String rawPassword) {
        User user = userService.findByEmail(email);

        if (user.getAuthProvider() != AuthProvider.EMAIL) {
            throw new BusinessException(ERR_SOCIAL_LOGIN_REQUIRED);
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ERR_INVALID_PASSWORD);
        }

        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ERR_INVALID_REFRESH_TOKEN);
        }

        Long userId = refreshTokenRedisRepository.findUserIdByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ERR_INVALID_REFRESH_TOKEN));

        User user = userService.findById(userId);

        refreshTokenRedisRepository.delete(refreshToken);

        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenRedisRepository.save(refreshToken, user.getId());

        return new TokenResponse(accessToken, refreshToken);
    }
}
