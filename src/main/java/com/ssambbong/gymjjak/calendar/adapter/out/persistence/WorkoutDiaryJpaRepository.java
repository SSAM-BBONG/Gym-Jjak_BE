package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutDiaryJpaRepository extends JpaRepository<WorkoutDiaryJpaEntity, Long> {

    Optional<WorkoutDiaryJpaEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "sets")
    @Query("""
        select distinct d
        from WorkoutDiaryJpaEntity d
        where d.userId = :userId
          and d.diaryDate = :date
        order by d.id asc
    """)
    List<WorkoutDiaryJpaEntity> findAllWithSetsByUserIdAndDiaryDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
        select new com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult(
            d.diaryDate,
            d.id,
            d.exercise
        )
        from WorkoutDiaryJpaEntity d
        where d.userId = :userId
          and d.diaryDate >= :startDate
          and d.diaryDate < :endDate
        order by d.diaryDate asc, d.id asc
    """)
    List<CalendarMonthDiaryResult> findDiarySummariesByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        select wd.diaryDate
        from WorkoutDiaryJpaEntity wd
        where wd.id = :workoutDiaryId
          and wd.userId = :userId
    """)
    Optional<LocalDate> findDiaryDateByUserIdAndWorkoutDiaryId(
            @Param("userId") Long userId,
            @Param("workoutDiaryId") Long workoutDiaryId
    );
}
