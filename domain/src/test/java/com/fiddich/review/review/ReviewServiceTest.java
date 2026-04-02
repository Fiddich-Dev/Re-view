package com.fiddich.review.review;

import com.fiddich.review.question.Question;
import com.fiddich.review.user.Platform;
import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewScheduleRepository reviewScheduleRepository;

    @Mock
    private ReviewResultRepository reviewResultRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Question question;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .platform(Platform.WEB)
                .build();

        question = mock(Question.class);
    }

    @Test
    @DisplayName("오늘 날짜와 PENDING 상태의 복습 일정을 조회한다")
    void findTodaySchedules() {
        // given
        given(reviewScheduleRepository.findAllByUserIdAndScheduledDateAndStatus(
                1L, LocalDate.now(), ScheduleStatus.PENDING))
                .willReturn(List.of());

        // when
        List<ReviewSchedule> result = reviewService.findTodaySchedules(1L);

        // then
        assertThat(result).isEmpty();
        verify(reviewScheduleRepository).findAllByUserIdAndScheduledDateAndStatus(
                1L, LocalDate.now(), ScheduleStatus.PENDING);
    }

    @Test
    @DisplayName("KNEW이고 마지막 단계가 아니면 다음 stage 스케줄을 생성한다")
    void completeReview_KNEW_중간단계() {
        // given
        ReviewSchedule schedule = ReviewSchedule.builder()
                .question(question)
                .user(user)
                .stage(2) // 중간 단계
                .build();

        given(reviewScheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
        given(reviewScheduleRepository.save(any(ReviewSchedule.class))).willAnswer(inv -> inv.getArgument(0));
        given(reviewResultRepository.save(any(ReviewResult.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        reviewService.completeReview(1L, ReviewAnswer.KNEW);

        // then
        ArgumentCaptor<ReviewSchedule> captor = ArgumentCaptor.forClass(ReviewSchedule.class);
        verify(reviewScheduleRepository).save(captor.capture());
        assertThat(captor.getValue().getStage()).isEqualTo(3);
    }

    @Test
    @DisplayName("KNEW이고 마지막 단계이면 추가 스케줄을 생성하지 않는다")
    void completeReview_KNEW_마지막단계() {
        // given
        ReviewSchedule schedule = ReviewSchedule.builder()
                .question(question)
                .user(user)
                .stage(ReviewSchedule.REVIEW_INTERVALS.length) // 마지막 단계
                .build();

        given(reviewScheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
        given(reviewResultRepository.save(any(ReviewResult.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        reviewService.completeReview(1L, ReviewAnswer.KNEW);

        // then
        verify(reviewScheduleRepository, never()).save(any(ReviewSchedule.class));
    }

    @Test
    @DisplayName("DIDNT_KNOW이면 stage 1로 초기화된 스케줄을 생성한다")
    void completeReview_DIDNT_KNOW() {
        // given
        ReviewSchedule schedule = ReviewSchedule.builder()
                .question(question)
                .user(user)
                .stage(3)
                .build();

        given(reviewScheduleRepository.findById(1L)).willReturn(Optional.of(schedule));
        given(reviewScheduleRepository.save(any(ReviewSchedule.class))).willAnswer(inv -> inv.getArgument(0));
        given(reviewResultRepository.save(any(ReviewResult.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        reviewService.completeReview(1L, ReviewAnswer.DIDNT_KNOW);

        // then
        ArgumentCaptor<ReviewSchedule> captor = ArgumentCaptor.forClass(ReviewSchedule.class);
        verify(reviewScheduleRepository).save(captor.capture());
        assertThat(captor.getValue().getStage()).isEqualTo(1);
        assertThat(captor.getValue().getIntervalDays()).isEqualTo(ReviewSchedule.REVIEW_INTERVALS[0]);
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 ID로 복습 완료 처리 시 예외가 발생한다")
    void completeReview_없는ID() {
        // given
        given(reviewScheduleRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.completeReview(999L, ReviewAnswer.KNEW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 복습 일정입니다.");
    }
}
