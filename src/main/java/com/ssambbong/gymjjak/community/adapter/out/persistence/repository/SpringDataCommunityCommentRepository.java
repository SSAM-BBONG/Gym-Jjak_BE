package com.ssambbong.gymjjak.community.adapter.out.persistence.repository;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataCommunityCommentRepository
        extends JpaRepository<CommunityCommentJpaEntity, Long> {

    Optional<CommunityCommentJpaEntity>
    findByIdAndDeletedAtIsNull(
            Long id
    );

    @Modifying
    @Query("""
            UPDATE CommunityCommentJpaEntity c
            SET c.content = :content,
                c.updatedAt = CURRENT_TIMESTAMP
            WHERE c.id = :commentId
              AND c.deletedAt IS NULL
            """)
    int updateCommunityComment(
            @Param("commentId") Long commentId,
            @Param("content") String content
    );

    @Modifying
    @Query(
            value = """
                UPDATE community_comments
                SET deleted_at = CURRENT_TIMESTAMP(6),
                    updated_at = CURRENT_TIMESTAMP(6)
                WHERE community_comment_id = :commentId
                  AND deleted_at IS NULL
                """,
            nativeQuery = true
    )
    int softDeleteCommunityCommentById(
            @Param("commentId") Long commentId
    );

    @Modifying
    @Query(
            value = """
                UPDATE community_comments
                SET deleted_at = NULL,
                    updated_at = CURRENT_TIMESTAMP(6)
                WHERE community_comment_id = :commentId
                  AND deleted_at IS NOT NULL
                """,
            nativeQuery = true
    )
    int restoreCommunityCommentById(
            @Param("commentId") Long commentId
    );

    @Query(
            value = """
                    SELECT community_comment_id
                    FROM community_comments
                    WHERE deleted_at IS NOT NULL
                      AND deleted_at < :threshold
                    ORDER BY deleted_at ASC,
                             community_comment_id ASC
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
                    DELETE FROM community_comments
                    WHERE community_comment_id IN :commentIds
                      AND deleted_at IS NOT NULL
                    """,
            nativeQuery = true
    )
    int hardDeleteByIds(
            @Param("commentIds") List<Long> commentIds
    );

    @Modifying
    @Query(
            value = """
                    DELETE FROM community_comments
                    WHERE community_post_id IN :postIds
                    """,
            nativeQuery = true
    )
    int hardDeleteByPostIds(
            @Param("postIds") List<Long> postIds
    );
}
