package com.fiddich.review.user;

import com.fiddich.review.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("이메일 중복이 없으면 회원가입에 성공한다")
    void register_성공() {
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        User user = userService.register("test@example.com", "encodedPassword", "홍길동", Platform.WEB, AuthProvider.EMAIL);

        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getPlatform()).isEqualTo(Platform.WEB);
        assertThat(user.getAuthProvider()).isEqualTo(AuthProvider.EMAIL);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 예외가 발생한다")
    void register_중복이메일() {
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        assertThatThrownBy(() -> userService.register("test@example.com", "encodedPassword", "홍길동", Platform.WEB, AuthProvider.EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserService.ERR_DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
    void findById_없는ID() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserService.ERR_USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회하면 예외가 발생한다")
    void findByEmail_없는이메일() {
        given(userRepository.findByEmail("none@example.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("none@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserService.ERR_USER_NOT_FOUND);
    }

    @Test
    @DisplayName("소셜 로그인으로 처음 가입하면 새 유저를 생성한다")
    void findOrCreateByOAuth_새_유저_생성() {
        given(userRepository.findByProviderIdAndAuthProvider("google_123", AuthProvider.GOOGLE))
                .willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        User user = userService.findOrCreateByOAuth(AuthProvider.GOOGLE, "google_123", "test@gmail.com", "홍길동");

        assertThat(user.getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(user.getProviderId()).isEqualTo("google_123");
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("소셜 로그인으로 이미 가입된 유저면 기존 유저를 반환한다")
    void findOrCreateByOAuth_기존_유저_반환() {
        User existing = User.builder()
                .email("test@gmail.com")
                .name("홍길동")
                .platform(Platform.APP)
                .authProvider(AuthProvider.GOOGLE)
                .providerId("google_123")
                .build();
        given(userRepository.findByProviderIdAndAuthProvider("google_123", AuthProvider.GOOGLE))
                .willReturn(Optional.of(existing));

        User user = userService.findOrCreateByOAuth(AuthProvider.GOOGLE, "google_123", "test@gmail.com", "홍길동");

        assertThat(user).isEqualTo(existing);
        verify(userRepository).findByProviderIdAndAuthProvider("google_123", AuthProvider.GOOGLE);
    }
}
