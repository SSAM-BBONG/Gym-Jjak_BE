package com.ssambbong.gymjjak.community.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "community_post_views",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_community_post_views_post_user",
                        columnNames = {
                                "community_post_id",
                                "user_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPostViewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_view_id")
    private Long id;

    @Column(name = "community_post_id", nullable = false)
    private Long communityPostId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;
}
