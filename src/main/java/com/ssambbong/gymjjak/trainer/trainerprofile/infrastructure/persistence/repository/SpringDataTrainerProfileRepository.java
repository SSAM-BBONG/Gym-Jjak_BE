package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface SpringDataTrainerProfileRepository extends JpaRepository<TrainerProfileJpaEntity, Long> {

    Optional<TrainerProfileJpaEntity> findByUserId(Long userId);

    @Query(value = """
        select
            result.trainerProfileId as trainerProfileId,
            result.name as name,
            result.username as username,
            result.nickname as nickname
        from (
            select
                tp.trainer_profile_id as trainerProfileId,
                u.name as name,
                u.username as username,
                u.nickname as nickname
            from trainer_profiles tp
            join users u
                on u.user_id = tp.user_id
            where tp.status = :status
              and u.username like concat(:keyword, '%')

            union

            select
                tp.trainer_profile_id as trainerProfileId,
                u.name as name,
                u.username as username,
                u.nickname as nickname
            from trainer_profiles tp
            join users u
                on u.user_id = tp.user_id
            where tp.status = :status
              and u.name like concat(:keyword, '%')

            union

            select
                tp.trainer_profile_id as trainerProfileId,
                u.name as name,
                u.username as username,
                u.nickname as nickname
            from trainer_profiles tp
            join users u
                on u.user_id = tp.user_id
            where tp.status = :status
              and u.nickname like concat(:keyword, '%')
        ) result
        order by result.name asc, result.trainerProfileId asc
        """, nativeQuery = true)
    Slice<SearchTrainerRow> searchTrainers(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    interface SearchTrainerRow {
        Long getTrainerProfileId();

        String getName();

        String getUsername();

        String getNickname();
    }


    // userId, status로 트레이너 프로필Id 조회
    @Query("""
        select tp.trainerProfileId
        from TrainerProfileJpaEntity tp
        where tp.userId = :userId
          and tp.status = :status
        """)
    Optional<Long> findTrainerProfileIdByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") TrainerProfileStatus status
    );

    // 트레이너 프로필 id, staus로 트레이너 이름 조회
    @Query("""
        select tp.trainerName
        from TrainerProfileJpaEntity tp
        where tp.trainerProfileId = :trainerProfileId
          and tp.status = :status
        """)
    Optional<String> findTrainerNameByIdAndStatus(
            @Param("trainerProfileId") Long trainerProfileId,
            @Param("status") TrainerProfileStatus status
    );

    // 강사평 - 트레이너 프로필 평균 평점 & 리뷰 수를 갱신
    @Modifying
    @Query("""
    UPDATE TrainerProfileJpaEntity tp
    SET tp.averageRating = :averageRating, tp.reviewCount = :reviewCount
    WHERE tp.trainerProfileId = :trainerProfileId
    """)
    int updateRatingStats(
            @Param("trainerProfileId") Long trainerProfileId,
            @Param("averageRating") BigDecimal averageRating,
            @Param("reviewCount") int reviewCount
            );

    // 목록 조회용 배치 조회 (ACTIVE 필터 포함)
    @Query("""
        SELECT tp FROM TrainerProfileJpaEntity tp
        WHERE tp.trainerProfileId IN :ids
          AND tp.status = :status
        """)
    List<TrainerProfileJpaEntity> findAllByIdsAndStatus(
            @Param("ids") List<Long> ids,
            @Param("status") TrainerProfileStatus status
    );

    // ACTIVE 상태 트레이너 수 집계
    long countByStatus(TrainerProfileStatus status);

    // ACTIVE 상태 트레이너 전체 평균 평점
    @Query("SELECT AVG(tp.averageRating) FROM TrainerProfileJpaEntity tp WHERE tp.status = :status")
    Double findAverageRatingByStatus(@Param("status") TrainerProfileStatus status);


}
