package com.fiddich.review.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReviewScheduleRepository extends JpaRepository<ReviewSchedule, Long> {

    List<ReviewSchedule> findAllByUserIdAndScheduledDateAndStatus(
            Long userId, LocalDate scheduledDate, ScheduleStatus status);

    List<ReviewSchedule> findAllByUserIdAndStatus(Long userId, ScheduleStatus status);
}
