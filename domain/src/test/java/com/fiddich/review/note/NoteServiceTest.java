package com.fiddich.review.note;

import com.fiddich.review.user.Platform;
import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NoteService noteService;

    @Test
    @DisplayName("유효한 유저 ID로 노트를 생성할 수 있다")
    void create_성공() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .platform(Platform.WEB)
                .build();
        given(userService.findById(1L)).willReturn(user);
        given(noteRepository.save(any(Note.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        noteService.create(1L, "Spring Boot 학습");

        // then
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
    void findById_없는ID() {
        // given
        given(noteRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noteService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 노트입니다.");
    }

    @Test
    @DisplayName("노트를 삭제하면 레포지토리의 deleteById가 호출된다")
    void delete() {
        // when
        noteService.delete(1L);

        // then
        verify(noteRepository).deleteById(1L);
    }
}
