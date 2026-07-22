package com.ssambbong.gymjjak.exercise.adapter.out.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseJpaRepository extends JpaRepository<ExerciseJpaEntity, Long> {

    boolean existsByPartAndExerciseName(PartType part, String exerciseName);

    boolean existsByPartAndExerciseNameAndIdNot(PartType part, String exerciseName, Long id);

    Optional<ExerciseJpaEntity> findByIdAndPart(Long id, PartType part);

    List<ExerciseJpaEntity> findAllByOrderByPartAscExerciseNameAsc();

    List<ExerciseJpaEntity> findByExerciseNameContainingIgnoreCaseOrderByPartAscExerciseNameAsc(
            String keyword
    );

    List<ExerciseJpaEntity> findByPartOrderByExerciseNameAsc(PartType part);

    List<ExerciseJpaEntity> findByPartAndExerciseNameContainingIgnoreCaseOrderByExerciseNameAsc(
            PartType part,
            String keyword
    );
}
