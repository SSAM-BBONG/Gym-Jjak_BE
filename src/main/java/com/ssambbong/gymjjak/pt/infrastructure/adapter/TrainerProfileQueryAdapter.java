package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.pt.domain.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 임시 구현체 !!
// 추후 트레이너프로필 구현되면 그때 수정할 것. 지금은 entityManager로 직접 쿼리
@Component
@RequiredArgsConstructor
public class TrainerProfileQueryAdapter implements TrainerProfileQueryPort {

    private final EntityManager em;


    @Override
    public TrainerInfo findByUserId(Long userId) {
        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT tp.trainer_profile_id, ot.organization_id
                FROM trainer_profiles tp
                JOIN organization_trainers ot ON tp.trainer_profile_id = ot.trainer_profile_id
                WHERE tp.user_id = :userId
                AND tp.status = 'ACTIVE'
                AND ot.removed_at IS NULL
                LIMIT 1
                """)
                .setParameter("userId", userId)
                .getSingleResult();

        return new TrainerInfo(
                ((Number) result[0]).longValue(),
                ((Number) result[1]).longValue()
        );
    }
}
