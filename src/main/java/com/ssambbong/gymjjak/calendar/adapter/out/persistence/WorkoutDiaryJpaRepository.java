package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import com.ssambbong.gymjjak.category.infrastructure.persistence.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutDiaryJpaRepository extends JpaRepository<WorkoutDiaryJpaEntity, Long> {

    boolean existsByUserIdAndDiaryDate(
            Long userId,
            LocalDate diaryDate
    );

    Optional<WorkoutDiaryJpaEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("""
        select new com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult(
             d.id,
             d.title,
             d.content,
             d.diaryDate,
             c.name
        )
        from WorkoutDiaryJpaEntity d
        join CategoryJpaEntity c on c.id = d.categoryId
        where d.userId = :userId
          and d.diaryDate = :date
    """)
    Optional<CalendarDayDiaryResult> findCalendarDayDiaryByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
    select new com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult(
        d.diaryDate,
        d.title
    )
    from WorkoutDiaryJpaEntity d
    where d.userId = :userId
      and d.diaryDate >= :startDate
      and d.diaryDate < :endDate
    order by d.diaryDate asc
""")
    List<CalendarMonthDiaryResult> findDiaryTitlesByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
