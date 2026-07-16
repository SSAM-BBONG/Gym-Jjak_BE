package com.ssambbong.gymjjak.community.adapter.out.persistence.repository;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityCommentProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostDetailProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataCommunityRepository
        extends JpaRepository<CommunityPostJpaEntity, Long> {

    @Query(
            value = """
                    SELECT
                        cp.community_post_id AS postId,
                        cp.type AS type,
                        cp.title AS title,
                        cp.content AS content,
                        u.nickname AS author,
                        cp.created_at AS createdAt,
                        cp.view_count AS viewCount,
                        COUNT(DISTINCT c.community_comment_id) AS commentCount,
                        COUNT(DISTINCT l.community_post_like_id) AS likeCount
                    FROM community_posts cp
                    JOIN users u
                        ON u.user_id = cp.user_id
                    LEFT JOIN community_comments c
                        ON c.community_post_id = cp.community_post_id
                        AND c.deleted_at IS NULL
                    LEFT JOIN community_post_likes l
                        ON l.community_post_id = cp.community_post_id
                    WHERE cp.deleted_at IS NULL
                      AND (:type IS NULL OR cp.type = :type)
                      AND (:keyword IS NULL OR cp.title LIKE CONCAT('%', :keyword, '%'))
                    GROUP BY
                        cp.community_post_id,
                        cp.type,
                        cp.title,
                        cp.content,
                        u.nickname,
                        cp.created_at,
                        cp.view_count
                    ORDER BY cp.created_at DESC,
                             cp.community_post_id DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM community_posts cp
                    WHERE cp.deleted_at IS NULL
                      AND (:type IS NULL OR cp.type = :type)
                      AND (:keyword IS NULL OR cp.title LIKE CONCAT('%', :keyword, '%'))
                    """,
            nativeQuery = true
    )
    Page<CommunityPostListProjection> findCommunityPosts(
            @Param("type") String type,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Modifying
    @Query(
            value = """
                    INSERT IGNORE INTO community_post_views (
                        community_post_id,
                        user_id
                    )
                    VALUES (
                        :postId,
                        :userId
                    )
                    """,
            nativeQuery = true
    )
    int insertViewIfAbsent(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    boolean existsByIdAndDeletedAtIsNull(
            Long id
    );

    @Modifying
    @Query(
            value = """
                    UPDATE community_posts
                    SET view_count = view_count + 1
                    WHERE community_post_id = :postId
                      AND deleted_at IS NULL
                    """,
            nativeQuery = true
    )
    int increaseViewCount(
            @Param("postId") Long postId
    );

    @Query(
            value = """
                    SELECT
                        cp.community_post_id AS postId,
                        cp.type AS type,
                        cp.title AS title,
                        cp.content AS content,
                        u.nickname AS author,
                        cp.created_at AS createdAt,
                        cp.view_count AS viewCount,

                        (
                            SELECT COUNT(*)
                            FROM community_post_likes l
                            WHERE l.community_post_id = cp.community_post_id
                        ) AS likeCount,

                        (
                            SELECT COUNT(*)
                            FROM community_comments c
                            WHERE c.community_post_id = cp.community_post_id
                              AND c.deleted_at IS NULL
                        ) AS commentCount,

                        CASE
                            WHEN cp.user_id = :userId
                            THEN 1
                            ELSE 0
                        END AS mine,

                        CASE
                            WHEN EXISTS (
                                SELECT 1
                                FROM community_post_likes l
                                WHERE l.community_post_id = cp.community_post_id
                                  AND l.user_id = :userId
                            )
                            THEN 1
                            ELSE 0
                        END AS likedByMe

                    FROM community_posts cp
                    JOIN users u
                        ON u.user_id = cp.user_id
                    WHERE cp.community_post_id = :postId
                      AND cp.deleted_at IS NULL
                    """,
            nativeQuery = true
    )
    Optional<CommunityPostDetailProjection> findCommunityPostDetail(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    @Query(
            value = """
                    SELECT
                        c.community_comment_id AS commentId,
                        u.nickname AS author,
                        c.created_at AS createdAt,
                        c.content AS content,

                        CASE
                            WHEN c.user_id = :userId
                            THEN 1
                            ELSE 0
                        END AS mine

                    FROM community_comments c
                    JOIN users u
                        ON u.user_id = c.user_id
                    WHERE c.community_post_id = :postId
                      AND c.deleted_at IS NULL
                      AND (
                          :cursorId IS NULL
                          OR c.community_comment_id > :cursorId
                      )
                    ORDER BY c.community_comment_id ASC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<CommunityCommentProjection> findCommunityCommentsByCursor(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            @Param("limit") int limit
    );

    Optional<CommunityPostJpaEntity> findByIdAndDeletedAtIsNull(
            Long id
    );

    @Modifying
    @Query("""
            UPDATE CommunityPostJpaEntity cp
            SET cp.title = :title,
                cp.content = :content
            WHERE cp.id = :postId
              AND cp.deletedAt IS NULL
            """)
    int updateCommunityPost(
            @Param("postId") Long postId,
            @Param("title") String title,
            @Param("content") String content
    );

    @Modifying
    @Query(
            value = """
                    UPDATE community_posts
                    SET deleted_at = CURRENT_TIMESTAMP(6),
                        updated_at = CURRENT_TIMESTAMP(6)
                    WHERE community_post_id = :postId
                      AND deleted_at IS NULL
                    """,
            nativeQuery = true
    )
    int deleteCommunityPostById(
            @Param("postId") Long postId
    );

    @Modifying
    @Query(
            value = """
                INSERT IGNORE INTO community_post_likes (
                    community_post_id,
                    user_id
                )
                VALUES (
                    :postId,
                    :userId
                )
                """,
            nativeQuery = true
    )
    int insertCommunityPostLikeIfAbsent(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query(
            value = """
                DELETE FROM community_post_likes
                WHERE community_post_id = :postId
                  AND user_id = :userId
                """,
            nativeQuery = true
    )
    int deleteCommunityPostLike(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query(
            value = """
                UPDATE community_posts
                SET deleted_at = NULL,
                    updated_at = CURRENT_TIMESTAMP(6)
                WHERE community_post_id = :postId
                  AND deleted_at IS NOT NULL
                """,
            nativeQuery = true
    )
    int restoreCommunityPostById(
            @Param("postId") Long postId
    );

    @Query(
            value = """
                    SELECT community_post_id
                    FROM community_posts
                    WHERE deleted_at IS NOT NULL
                      AND deleted_at < :threshold
                    ORDER BY deleted_at ASC,
                             community_post_id ASC
                    LIMIT :batchSize
                    """,
            nativeQuery = true
    )
    List<Long> findHardDeleteCandidateIds(
            @Param("threshold") LocalDateTime threshold,
            @Param("batchSize") int batchSize
    );

    @Modifying
    @Query(
            value = """
                    DELETE FROM community_post_views
                    WHERE community_post_id IN :postIds
                    """,
            nativeQuery = true
    )
    int hardDeletePostViewsByPostIds(
            @Param("postIds") List<Long> postIds
    );

    @Modifying
    @Query(
            value = """
                    DELETE FROM community_post_likes
                    WHERE community_post_id IN :postIds
                    """,
            nativeQuery = true
    )
    int hardDeletePostLikesByPostIds(
            @Param("postIds") List<Long> postIds
    );

    @Modifying
    @Query(
            value = """
                    DELETE FROM community_posts
                    WHERE community_post_id IN :postIds
                      AND deleted_at IS NOT NULL
                    """,
            nativeQuery = true
    )
    int hardDeleteByIds(
            @Param("postIds") List<Long> postIds
    );
}
