package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutDiarySetJpaRepository extends JpaRepository<WorkoutDiarySetJpaEntity, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = false)
    @Query("""
        delete from WorkoutDiarySetJpaEntity s
        where s.workoutDiary.id = :workoutDiaryId
    """)
    void deleteByWorkoutDiaryId(@Param("workoutDiaryId") Long workoutDiaryId);
}
