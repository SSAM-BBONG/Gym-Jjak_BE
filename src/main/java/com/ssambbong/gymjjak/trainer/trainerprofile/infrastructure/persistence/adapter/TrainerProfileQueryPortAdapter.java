package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerCertificationJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// pt 조회/통계 시 트레이너 프로필 정보를 조회하는 어댑터
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerProfileQueryPortAdapter implements TrainerProfileQueryPort {

    private final SpringDataTrainerProfileRepository trainerProfileRepository;
    private final SpringDataTrainerCertificationRepository certificationRepository;
    private final SpringDataTrainerAwardRepository awardRepository;
    private final EntityManager em;

    // TODO : 추후 삭제 부탁드립니다.
    // userId로 트레이너 프로필 ID와 소속 조직 ID 조회
    /**
     * @deprecated 조직 정보까지 함께 조회하는 기존 메서드입니다.
     * 신규 로직에서는 findActiveTrainerProfileIdByUserId(Long userId)를 사용하세요.
     */
    @Deprecated
    @Override
    public TrainerInfo findByUserId(Long userId) {
        Long trainerProfileId = findActiveTrainerProfileIdByUserId(userId);

        return new TrainerInfo(
                trainerProfileId,
                null
        );
    }

    // userId로 활성화된 트레이너 프로필 ID 조회
    @Override
    public Long findActiveTrainerProfileIdByUserId(Long userId) {
        return trainerProfileRepository
                .findTrainerProfileIdByUserIdAndStatus(
                        userId,
                        TrainerProfileStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new TrainerProfileNotFoundException("userId", userId)
                );
    }

    // ACTIVE 상태 트레이너 수
    // TODO: 통계 대시보드 태스트 후, trainerProfile index 추가 고려해도 될듯
    @Override
    public long countActive() {
        return trainerProfileRepository
                .countByStatus(TrainerProfileStatus.ACTIVE);
    }

    // 전체 평균 평점
    @Override
    public Double averageRating() {
        return trainerProfileRepository
                .findAverageRatingByStatus(TrainerProfileStatus.ACTIVE);
    }

    // 목록 조회용 트레이너 이름 반환
    @Override
    public String findTrainerNameById(Long trainerProfileId) {
        return trainerProfileRepository
                .findTrainerNameByIdAndStatus(
                        trainerProfileId,
                        TrainerProfileStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new TrainerProfileNotFoundException(
                                "trainerProfileId",
                                trainerProfileId
                        )
                );
    }


    // TODO: 이거 지우고, 아래로 연결
    // 목록 조회용 요약 정보 배치 조회 (N+1 방지)
    @Override
    public Map<Long, TrainerSummaryInfo> findSummaryAllByIds(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();

        return trainerProfileRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        TrainerProfileJpaEntity::getTrainerProfileId,
                        e -> new TrainerSummaryInfo(
                                e.getTrainerName(),
                                e.getAverageRating() != null ? e.getAverageRating().doubleValue() : null,
                                e.getReviewCount()
                        )
                ));
    }

//    @Query("""
//        select new com.ssambbong.gymjjak.pt.ptCourse.application.port.dto.TrainerSummaryInfo(
//            tp.trainerProfileId,
//            tp.trainerName,
//            tp.reviewCount
//        )
//        from TrainerProfileJpaEntity tp
//        where tp.trainerProfileId in :ids
//          and tp.status = :status
//        """)
//    List<TrainerSummaryInfo> findSummariesByIdsAndStatus(
//            @Param("ids") List<Long> ids,
//            @Param("status") TrainerProfileStatus status
//    );

    // 상세 조회용 전체 정보 조회 (자격증, 수상 이력 포함)
    @Override
    public TrainerDisplayInfo findById(Long trainerProfileId) {
        TrainerProfileJpaEntity entity = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() ->
                        new TrainerProfileNotFoundException(
                                "trainerProfileId",
                                trainerProfileId
                        )
                );

        List<String> certifications = certificationRepository
                .findAllByTrainerProfileIdOrderByTrainerCertificationIdAsc(trainerProfileId)
                .stream()
                .map(TrainerCertificationJpaEntity::getName)
                .toList();

        List<String> awards = awardRepository
                .findAllByTrainerProfileIdOrderByTrainerAwardIdAsc(trainerProfileId)
                .stream()
                .map(TrainerAwardJpaEntity::getName)
                .toList();

        return new TrainerDisplayInfo(
                entity.getTrainerName(),
                entity.getIntroduction(),
                entity.getAverageRating() != null ? entity.getAverageRating().doubleValue() : null,
                entity.getReviewCount(),
                entity.getProfileFileId(),
                certifications,
                awards
        );
    }
}
