package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface SpringDataTrainerApplicationRepository extends JpaRepository<TrainerApplicationJpaEntity, Long> {

    // 이미 대기중 / 승인된 신청서 존재 여부 확인
    boolean existsByUserIdAndStatusIn(
            Long userId,
            Collection<TrainerApplicationStatus> statuses
    );

    Optional<TrainerApplicationJpaEntity> findTopByUserIdOrderByCreatedAtDescTrainerApplicationIdDesc(Long userId);

    // 관리자 목록 조회 Query
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
                where ta.status = :status
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
                    where ta.status = :status
                    and (
                         :keyword is null
                         or u.username like concat('%', :keyword, '%')
                         or u.name like concat('%', :keyword, '%')
                         or u.nickname like concat('%', :keyword, '%')
                    )
            """
    )
    Page<TrainerApplicationSummaryResult> findTrainerApplicationSummaries(
            @Param("status") TrainerApplicationStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        select new com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult(
            ta.trainerApplicationId, ta.userId, ta.profileFileId, u.name, u.username, u.nickname,
            ta.introduction, ta.qualifications, ta.certificateFileId, ta.awardHistories, ta.status
            )
        from TrainerApplicationJpaEntity ta
        join UserJpaEntity u on u.id = ta.userId
        where ta.trainerApplicationId = :trainerApplicationId
    """)
    Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(
            @Param("trainerApplicationId") Long trainerApplicationId);

    @Lock(LockModeType.WRITE)
    @Query("""
            select ta
            from TrainerApplicationJpaEntity ta
            where ta.trainerApplicationId = :trainerApplicationId
    """)
    Optional<TrainerApplicationJpaEntity> findByIdForUpdate(
            @Param("trainerApplicationId") Long trainerApplicationId
    );
}
