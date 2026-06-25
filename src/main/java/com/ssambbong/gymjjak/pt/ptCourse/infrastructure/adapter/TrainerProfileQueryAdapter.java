package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.TrainerProfileNotFoundException;
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

    // 활성화 된 트레이너 수
    @Override
    public long countActive() {
        Number result = (Number) em.createNativeQuery("""
                SELECT COUNT(*) FROM trainer_profiles
                WHERE status = 'ACTIVE' AND deleted_at IS NULL
                """)
                .getSingleResult();
        return result.longValue();
    }

    // 평균 만족도
    @Override
    public Double averageRating() {
        Object result = em.createNativeQuery("""
                SELECT AVG(average_rating) FROM trainer_profiles
                WHERE status = 'ACTIVE' AND deleted_at IS NULL
                """)
                .getSingleResult();
        return result != null ? ((Number) result).doubleValue() : null;
    }

    @Override
    public TrainerInfo findByUserId(Long userId) {
        List<?> results = em.createNativeQuery("""
                SELECT tp.trainer_profile_id, ot.organization_id
                FROM trainer_profiles tp
                JOIN organization_trainers ot ON tp.trainer_profile_id = ot.trainer_profile_id
                WHERE tp.user_id = :userId
                AND tp.status = 'ACTIVE'
                AND tp.deleted_at IS NULL
                AND ot.removed_at IS NULL
                LIMIT 1
                """)
                .setParameter("userId", userId)
                .getResultList();

        Object[] result = (Object[]) results.stream()
                .findFirst()
                .orElseThrow(TrainerProfileNotFoundException::new);

        return new TrainerInfo(
                ((Number) result[0]).longValue(),
                ((Number) result[1]).longValue()
        );
    }

    // 목록 조회용: trainerName, reviewCount만 조회 (자격증/수상 쿼리 없음)
    @Override
    public TrainerSummaryInfo findSummaryById(Long trainerProfileId) {
        List<?> results = em.createNativeQuery("""
                SELECT tp.trainer_name, tp.review_count
                FROM trainer_profiles tp
                WHERE tp.trainer_profile_id = ?1
                  AND tp.deleted_at IS NULL
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        Object[] result = (Object[]) results.stream()
                .findFirst()
                .orElseThrow(TrainerProfileNotFoundException::new);

        return new TrainerSummaryInfo(
                (String) result[0],
                result[1] != null ? ((Number) result[1]).intValue() : 0
        );
    }

    // 상세 조회용: 전체 정보 조회 (자격증/수상 포함)
    @Override
    public TrainerDisplayInfo findById(Long trainerProfileId) {
        List<?> results = em.createNativeQuery("""
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
                .getResultList();

        Object[] result = (Object[]) results.stream()
                .findFirst()
                .orElseThrow(TrainerProfileNotFoundException::new);

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
