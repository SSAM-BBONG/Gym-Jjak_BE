package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface SpringDataTrainerApplicationRepository extends JpaRepository<TrainerApplicationJpaEntity, Long> {

    // 이미 대기중 / 승인된 신청서 존재 여부 확인
    boolean existsByUserIdAndStatusIn(
            Long userId,
            Collection<TrainerApplicationStatus> statuses
    );
}
