package com.ssambbong.gymjjak.community.adapter.out.persistence.entity;

import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CommunityPostJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_post_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CommunityPostType type;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private CommunityPostJpaEntity(
            Long userId,
            CommunityPostType type,
            String title,
            String content,
            Long viewCount
    ) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public static CommunityPostJpaEntity create(
            Long userId,
            CommunityPostType type,
            String title,
            String content,
            Long viewCount
    ) {

        return new CommunityPostJpaEntity(
                userId,
                type,
                title,
                content,
                viewCount
        );
    }
}
