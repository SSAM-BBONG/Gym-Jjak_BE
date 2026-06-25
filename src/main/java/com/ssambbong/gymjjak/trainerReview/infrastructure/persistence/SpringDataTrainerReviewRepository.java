package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataTrainerReviewRepository extends JpaRepository<TrainerReviewJpaEntity, Long> {

    boolean existsByPtReservationIdAndDeletedAtIsNull(Long ptReservationId);

    @Query(value = "SELECT COUNT(*) FROM trainer_reviews WHERE deleted_at IS NULL", nativeQuery = true)
    long countActive();

    @Query(value = "SELECT COALESCE(AVG(rating), 0) FROM trainer_reviews WHERE deleted_at IS NULL", nativeQuery = true)
    double findAverageRating();

    Optional<TrainerReviewJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = """
            SELECT
                tr.trainer_review_id AS trainerReviewId,
                u.nickname           AS nickname,
                pc.title             AS ptCourseTitle,
                tr.rating            AS rating,
                tr.content           AS content,
                tr.created_at        AS createdAt
            FROM trainer_reviews tr
            JOIN users u  ON tr.user_id = u.user_id
            JOIN pt_courses pc ON tr.pt_course_id = pc.pt_course_id
            WHERE tr.trainer_profile_id = :trainerProfileId
              AND tr.deleted_at IS NULL
              AND (:cursor IS NULL OR tr.trainer_review_id < :cursor)
            ORDER BY tr.trainer_review_id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<TrainerReviewProjection> findLatestReviews(
            @Param("trainerProfileId") Long trainerProfileId,
            @Param("cursor") Long cursor,
            @Param("size") int size
    );

    @Query(value = """
            SELECT
                tr.trainer_review_id AS trainerReviewId,
                u.nickname           AS nickname,
                pc.title             AS ptCourseTitle,
                tr.rating            AS rating,
                tr.content           AS content,
                tr.created_at        AS createdAt
            FROM trainer_reviews tr
            JOIN users u  ON tr.user_id = u.user_id
            JOIN pt_courses pc ON tr.pt_course_id = pc.pt_course_id
            WHERE tr.trainer_profile_id = :trainerProfileId
              AND tr.deleted_at IS NULL
              AND (
                :cursor IS NULL
                OR :cursorRating IS NULL
                OR tr.rating < :cursorRating
                OR (tr.rating = :cursorRating AND tr.trainer_review_id < :cursor)
              )
            ORDER BY tr.rating DESC, tr.trainer_review_id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<TrainerReviewProjection> findHighRatingReviews(
            @Param("trainerProfileId") Long trainerProfileId,
            @Param("cursor") Long cursor,
            @Param("cursorRating") Integer cursorRating,
            @Param("size") int size
    );

    @Query(value = """
            SELECT
                tp.trainer_name                                              AS trainerName,
                tp.introduction                                              AS introduction,
                COALESCE(AVG(tr.rating), 0)                                  AS averageRating,
                COUNT(tr.trainer_review_id)                                  AS reviewCount,
                SUM(CASE WHEN tr.rating = 5 THEN 1 ELSE 0 END)              AS rating5Count,
                SUM(CASE WHEN tr.rating = 4 THEN 1 ELSE 0 END)              AS rating4Count,
                SUM(CASE WHEN tr.rating = 3 THEN 1 ELSE 0 END)              AS rating3Count,
                SUM(CASE WHEN tr.rating = 2 THEN 1 ELSE 0 END)              AS rating2Count,
                SUM(CASE WHEN tr.rating = 1 THEN 1 ELSE 0 END)              AS rating1Count
            FROM trainer_profiles tp
            LEFT JOIN trainer_reviews tr ON tp.trainer_profile_id = tr.trainer_profile_id
                AND tr.deleted_at IS NULL
            WHERE tp.trainer_profile_id = :trainerProfileId
            GROUP BY tp.trainer_profile_id, tp.trainer_name, tp.introduction
            """, nativeQuery = true)
    TrainerReviewSummaryProjection findSummary(@Param("trainerProfileId") Long trainerProfileId);
}
