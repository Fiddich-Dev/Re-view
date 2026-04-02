package com.fiddich.review.review;

import com.fiddich.review.common.exception.BusinessException;
import com.fiddich.review.question.Question;
import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    public static final String ERR_SCHEDULE_NOT_FOUND = "존재하지 않는 복습 일정입니다.";

    private final ReviewScheduleRepository reviewScheduleRepository;
    private final ReviewResultRepository reviewResultRepository;
    private final UserService userService;

    public List<ReviewSchedule> findTodaySchedules(Long userId) {
        return reviewScheduleRepository.findAllByUserIdAndScheduledDateAndStatus(
                userId, LocalDate.now(), ScheduleStatus.PENDING);
    }

    @Transactional
    public ReviewSchedule createInitialSchedule(Question question, User user) {
        ReviewSchedule schedule = ReviewSchedule.builder()
                .question(question)
                .user(user)
                .stage(1)
                .build();
        return reviewScheduleRepository.save(schedule);
    }

    @Transactional
    public void completeReview(Long scheduleId, ReviewAnswer answer) {
        ReviewSchedule schedule = reviewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ERR_SCHEDULE_NOT_FOUND));

        schedule.complete();
        reviewResultRepository.save(ReviewResult.builder()
                .reviewSchedule(schedule)
                .answer(answer)
                .build());

        if (answer == ReviewAnswer.KNEW && !schedule.isLastStage()) {
            // 다음 단계 스케줄 생성
            reviewScheduleRepository.save(ReviewSchedule.builder()
                    .question(schedule.getQuestion())
                    .user(schedule.getUser())
                    .stage(schedule.getStage() + 1)
                    .build());
        } else if (answer == ReviewAnswer.DIDNT_KNOW) {
            // 1단계로 초기화
            reviewScheduleRepository.save(ReviewSchedule.builder()
                    .question(schedule.getQuestion())
                    .user(schedule.getUser())
                    .stage(1)
                    .build());
        }
    }
}
