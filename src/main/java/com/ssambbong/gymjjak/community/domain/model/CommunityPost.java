package com.ssambbong.gymjjak.community.domain.model;

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
        return new CommunityPost(
                null,
                userId,
                type,
                title,
                content,
                0L
        );
    }
}
