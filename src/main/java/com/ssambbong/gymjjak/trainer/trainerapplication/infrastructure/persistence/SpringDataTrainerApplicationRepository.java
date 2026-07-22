package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpringDataTrainerApplicationRepository extends JpaRepository<TrainerApplicationJpaEntity, Long> {

    // 본인 트레이너 신청서 일치 여부 검증 메서드
    Optional<TrainerApplicationJpaEntity> findByTrainerApplicationIdAndUserId(
            Long trainerApplicationId,
            Long userId
    );

    // 트레이너 신청 목록 조회 (헬스장이름 join)
    @Query(
            value = """
                    select new com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationSummaryResult(
                        ta.trainerApplicationId,
                        o.businessName,
                        ta.status,
                        ta.createdAt,
                        ta.reviewedAt,
                        ta.rejectReason
                    )
                    from TrainerApplicationJpaEntity ta
                    join OrganizationJpaEntity o on o.organizationId = ta.organizationId
                    where ta.userId = :userId
                    order by ta.createdAt desc, ta.trainerApplicationId desc
                    """,
            countQuery = """
                    select count(ta)
                    from TrainerApplicationJpaEntity ta
                    where ta.userId = :userId
                    """
    )
    Page<MyTrainerApplicationSummaryResult> findMyTrainerApplicationSummaries(
            @Param("userId") Long userId,
            Pageable pageable
    );

    // 조직별 트레이너 신청 목록 조회 기능
    @Query(
            value = """
                select new com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult(
                    ta.trainerApplicationId,
                    ta.userId,
                    u.username,
                    u.name,
                    u.nickname,
                    ta.status,
                    ta.createdAt,
                    ta.reviewedAt
                )
                from TrainerApplicationJpaEntity ta
                join UserJpaEntity u on u.id = ta.userId
                where ta.organizationId = :organizationId
                and ta.status = :status
                  and (
                       :keyword is null
                       or u.username like concat('%', :keyword, '%')
                       or u.name like concat('%', :keyword, '%')
                       or u.nickname like concat('%', :keyword, '%')
                  )
                order by ta.createdAt asc, ta.trainerApplicationId asc
                """,
            countQuery = """
                    select count(ta)
                    from TrainerApplicationJpaEntity ta
                    join UserJpaEntity  u on u.id = ta.userId
                    where ta.organizationId = :organizationId
                    and ta.status = :status
                    and (
                         :keyword is null
                         or u.username like concat('%', :keyword, '%')
                         or u.name like concat('%', :keyword, '%')
                         or u.nickname like concat('%', :keyword, '%')
                    )
            """
    )
    Page<TrainerApplicationSummaryResult> findTrainerApplicationSummaries(
            @Param("organizationId") Long organizationId,
            @Param("status") TrainerApplicationStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 조직별 트레이너 신청 상세 조회 기능
    @Query("""
        select new com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult(
            ta.trainerApplicationId,
            ta.userId,
            ta.profileFileId,
            u.name,
            u.username,
            u.nickname,
            ta.introduction,
            ta.qualifications,
            ta.certificateFileId,
            ta.awardHistories,
            ta.status
        )
        from TrainerApplicationJpaEntity ta
        join UserJpaEntity u on u.id = ta.userId
        where ta.trainerApplicationId = :trainerApplicationId
          and ta.organizationId = :organizationId
    """)
    Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(
            @Param("trainerApplicationId") Long trainerApplicationId,
            @Param("organizationId") Long organizationId
    );

    // 승인/반려 처리 시 동시성 충돌을 방지
    // 트레이너 신청서 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ta
            from TrainerApplicationJpaEntity ta
            where ta.trainerApplicationId = :trainerApplicationId
    """)
    Optional<TrainerApplicationJpaEntity> findByIdForUpdate(
            @Param("trainerApplicationId") Long trainerApplicationId
    );

    long countByStatus(TrainerApplicationStatus status);

    boolean existsByUserIdAndOrganizationIdInAndStatusIn(
            Long userId,
            List<Long> organizationIds,
            List<TrainerApplicationStatus> statuses
    );
}
