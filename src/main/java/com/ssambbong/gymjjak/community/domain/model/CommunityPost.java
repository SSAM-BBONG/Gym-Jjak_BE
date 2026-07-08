package com.ssambbong.gymjjak.community.domain.model;

import com.ssambbong.gymjjak.community.domain.exception.CommunityErrorCode;
import com.ssambbong.gymjjak.community.domain.exception.CommunityException;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import lombok.Getter;

@Getter
public class CommunityPost {

    private final Long id;
    private final Long userId;
    private final CommunityPostType type;
    private String title;
    private String content;
    private Long viewCount;

    private CommunityPost(
            Long id,
            Long userId,
            CommunityPostType type,
            String title,
            String content,
            Long viewCount
    ) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public static CommunityPost create(
            Long userId,
            CommunityPostType type,
            String title,
            String content
    ) {
        if (title == null || title.isBlank()) {
            throw new CommunityException(
                    CommunityErrorCode.TITLE_REQUIRED);
        }

        if (content == null || content.isBlank()) {
            throw new CommunityException(
                    CommunityErrorCode.CONTENT_REQUIRED);
        }

        return new CommunityPost(
                null,
                userId,
                type,
                title,
                content,
                0L
        );
    }

    public static CommunityPost reconstruct(
            Long id,
            Long userId,
            CommunityPostType type,
            String title,
            String content,
            Long viewCount
    ) {

        if (title == null || title.isBlank()) {
            throw new CommunityException(
                    CommunityErrorCode.TITLE_REQUIRED);
        }

        if (content == null || content.isBlank()) {
            throw new CommunityException(
                    CommunityErrorCode.CONTENT_REQUIRED);
        }

        return new CommunityPost(
                id,
                userId,
                type,
                title,
                content,
                viewCount
        );
    }

    public void update(
            String title,
            String content
    ) {

        this.title = title;
        this.content = content;
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }
}
