package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.category.infrastructure.persistence.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkoutDiaryJpaRepository extends JpaRepository<WorkoutDiaryJpaEntity, Long> {

    boolean existsByUserIdAndDiaryDate(
            Long userId,
            LocalDate diaryDate
    );

    Optional<WorkoutDiaryJpaEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

}
