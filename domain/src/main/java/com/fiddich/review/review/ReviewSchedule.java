package com.fiddich.review.review;

import com.fiddich.review.common.BaseEntity;
import com.fiddich.review.common.exception.BusinessException;
import com.fiddich.review.question.Question;
import com.fiddich.review.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "review_schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSchedule extends BaseEntity {

    // 망각곡선 기반 복습 주기 (일 단위)
    public static final int[] REVIEW_INTERVALS = {1, 3, 7, 21, 30};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int stage; // 1 ~ 5 (REVIEW_INTERVALS 인덱스 기반)

    private int intervalDays; // 1, 3, 7, 21, 30

    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Builder
    private ReviewSchedule(Question question, User user, int stage) {
        if (stage < 1 || stage > REVIEW_INTERVALS.length) {
            throw new BusinessException("유효하지 않은 복습 단계입니다.");
        }
        this.question = question;
        this.user = user;
        this.stage = stage;
        this.intervalDays = REVIEW_INTERVALS[stage - 1];
        this.scheduledDate = LocalDate.now().plusDays(this.intervalDays);
        this.status = ScheduleStatus.PENDING;
    }

    public void complete() {
        this.status = ScheduleStatus.COMPLETED;
    }

    public void skip() {
        this.status = ScheduleStatus.SKIPPED;
    }

    public boolean isLastStage() {
        return this.stage >= REVIEW_INTERVALS.length;
    }
}
