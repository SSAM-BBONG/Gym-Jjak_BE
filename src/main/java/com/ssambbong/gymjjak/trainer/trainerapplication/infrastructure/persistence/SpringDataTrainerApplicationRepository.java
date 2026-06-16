package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
                       or lower(u.username) like lower(concat('%', :keyword, '%'))
                       or lower(u.name) like lower(concat('%', :keyword, '%'))
                       or lower(u.nickname) like lower(concat('%', :keyword, '%'))
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
                        or lower(u.username) like lower(concat('%', :keyword, '%'))
                        or lower(u.name) like lower(concat('%', :keyword, '%'))
                        or lower(u.nickname) like lower(concat('%', :keyword, '%'))
                    )
            """
    )
    Page<TrainerApplicationSummaryResult> findTrainerApplicationSummaries(
            @Param("status") TrainerApplicationStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
