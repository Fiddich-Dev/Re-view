package com.fiddich.review.user;

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
        // given
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        User user = userService.register("test@example.com", "password", "홍길동", Platform.WEB);

        // then
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getPlatform()).isEqualTo(Platform.WEB);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 예외가 발생한다")
    void register_중복이메일() {
        // given
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.register("test@example.com", "password", "홍길동", Platform.WEB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
    void findById_없는ID() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}
