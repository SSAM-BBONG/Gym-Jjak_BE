package com.ssambbong.gymjjak.community.adapter.out.persistence.repository;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataCommunityRepository extends JpaRepository<CommunityPostJpaEntity, Long> {

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
                    GROUP BY
                        cp.community_post_id,
                        cp.type,
                        cp.title,
                        cp.content,
                        u.nickname,
                        cp.created_at,
                        cp.view_count
                    ORDER BY cp.created_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM community_posts cp
                    WHERE cp.deleted_at IS NULL
                        AND (:type IS NULL OR cp.type = :type)
                    """,
            nativeQuery = true
    )
    Page<CommunityPostListProjection> findCommunityPosts(
            @Param("type") String type,
            Pageable pageable
    );
}
