package com.ssambbong.gymjjak.community.adapter.out.persistence.repository;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
