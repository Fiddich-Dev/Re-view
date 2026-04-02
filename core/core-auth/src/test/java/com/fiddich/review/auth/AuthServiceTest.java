package com.fiddich.review.auth;

import com.fiddich.review.common.exception.BusinessException;
import com.fiddich.review.redis.RefreshTokenRedisRepository;
import com.fiddich.review.user.AuthProvider;
import com.fiddich.review.user.Platform;
import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User emailUser() {
        return User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .name("홍길동")
                .platform(Platform.WEB)
                .authProvider(AuthProvider.EMAIL)
                .build();
    }

    @Test
    @DisplayName("이메일/비밀번호 로그인에 성공하면 토큰을 반환한다")
    void login_성공() {
        given(userService.findByEmail("test@example.com")).willReturn(emailUser());
        given(passwordEncoder.matches("rawPassword", "encoded_password")).willReturn(true);
        given(jwtProvider.generateAccessToken(any())).willReturn("access_token");
        given(jwtProvider.generateRefreshToken(any())).willReturn("refresh_token");

        TokenResponse response = authService.login("test@example.com", "rawPassword");

        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.refreshToken()).isEqualTo("refresh_token");
        verify(refreshTokenRedisRepository).save(anyString(), any());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 예외가 발생한다")
    void login_잘못된_비밀번호() {
        given(userService.findByEmail("test@example.com")).willReturn(emailUser());
        given(passwordEncoder.matches("wrongPassword", "encoded_password")).willReturn(false);

        assertThatThrownBy(() -> authService.login("test@example.com", "wrongPassword"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.ERR_INVALID_PASSWORD);
    }

    @Test
    @DisplayName("소셜 로그인 유저가 이메일 로그인 시도 시 예외가 발생한다")
    void login_소셜_유저_로그인_시도() {
        User googleUser = User.builder()
                .email("test@gmail.com")
                .name("홍길동")
                .platform(Platform.APP)
                .authProvider(AuthProvider.GOOGLE)
                .providerId("google_123")
                .build();
        given(userService.findByEmail("test@gmail.com")).willReturn(googleUser);

        assertThatThrownBy(() -> authService.login("test@gmail.com", "anyPassword"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.ERR_SOCIAL_LOGIN_REQUIRED);
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 새 토큰을 발급한다")
    void refresh_성공() {
        given(jwtProvider.validateToken("valid_refresh_token")).willReturn(true);
        given(refreshTokenRedisRepository.getAndDelete("valid_refresh_token")).willReturn(Optional.of(1L));
        given(userService.findById(1L)).willReturn(emailUser());
        given(jwtProvider.generateAccessToken(any())).willReturn("new_access_token");
        given(jwtProvider.generateRefreshToken(any())).willReturn("new_refresh_token");

        TokenResponse response = authService.refresh("valid_refresh_token");

        assertThat(response.accessToken()).isEqualTo("new_access_token");
        verify(refreshTokenRedisRepository).save(anyString(), any());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이면 예외가 발생한다")
    void refresh_유효하지_않은_토큰() {
        given(jwtProvider.validateToken("invalid_token")).willReturn(false);

        assertThatThrownBy(() -> authService.refresh("invalid_token"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.ERR_INVALID_REFRESH_TOKEN);
    }
}
