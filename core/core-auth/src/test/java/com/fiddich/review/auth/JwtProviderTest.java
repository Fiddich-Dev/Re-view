package com.fiddich.review.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                "test-secret-key-for-jwt-must-be-at-least-32-bytes!!",
                1800000L,
                604800000L
        );
    }

    @Test
    @DisplayName("액세스 토큰에서 userId를 추출할 수 있다")
    void generateAccessToken_userId_추출가능() {
        String token = jwtProvider.generateAccessToken(1L);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("리프레시 토큰에서 userId를 추출할 수 있다")
    void generateRefreshToken_userId_추출가능() {
        String token = jwtProvider.generateRefreshToken(1L);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("유효한 토큰은 검증에 성공한다")
    void validateToken_유효한_토큰() {
        String token = jwtProvider.generateAccessToken(1L);

        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 검증에 실패한다")
    void validateToken_잘못된_형식() {
        assertThat(jwtProvider.validateToken("invalid.token.value")).isFalse();
    }
}
