package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// 임시 구현체 !!
// 추후 트레이너프로필 구현되면 그때 수정할 것. 지금은 entityManager로 직접 쿼리
// TODO: TrainerProfile 도메인 Query Port 구현 후 EntityManager 직접 조회 제거
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

    @Override
    public TrainerDisplayInfo findById(Long trainerProfileId) {
        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT tp.trainer_name,
                       tp.introduction,
                       tp.average_rating,
                       tp.review_count,
                       tp.profile_file_id
                FROM trainer_profiles tp
                WHERE tp.trainer_profile_id = ?1
                  AND tp.deleted_at IS NULL
                """)
                .setParameter(1, trainerProfileId)
                .getSingleResult();

        List<String> certifications = findTrainerCertificationNames(trainerProfileId);
        List<String> awards = findTrainerAwardNames(trainerProfileId);

        return new TrainerDisplayInfo(
                (String) result[0],
                (String) result[1],
                result[2] != null ? ((Number) result[2]).doubleValue() : null,
                result[3] != null ? ((Number) result[3]).intValue() : 0,
                result[4] != null ? ((Number) result[4]).longValue() : null,
                certifications,
                awards
        );
    }

    private List<String> findTrainerCertificationNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
                SELECT tc.name
                FROM trainer_certifications tc
                WHERE tc.trainer_profile_id = ?1
                  AND tc.deleted_at IS NULL
                ORDER BY tc.trainer_certification_id
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }

    private List<String> findTrainerAwardNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
                SELECT ta.name
                FROM trainer_awards ta
                WHERE ta.trainer_profile_id = ?1
                  AND ta.deleted_at IS NULL
                ORDER BY ta.trainer_award_id
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }
}
