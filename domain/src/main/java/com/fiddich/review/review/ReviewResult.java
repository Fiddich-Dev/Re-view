package com.fiddich.review.review;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_schedule_id", nullable = false)
    private ReviewSchedule reviewSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewAnswer answer;

    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    @Builder
    private ReviewResult(ReviewSchedule reviewSchedule, ReviewAnswer answer) {
        this.reviewSchedule = reviewSchedule;
        this.answer = answer;
        this.reviewedAt = LocalDateTime.now();
    }
}
