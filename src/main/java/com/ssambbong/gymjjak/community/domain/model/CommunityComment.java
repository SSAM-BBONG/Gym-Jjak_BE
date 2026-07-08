package com.ssambbong.gymjjak.community.domain.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CommunityComment {

    private final Long id;
    private final Long postId;
    private final Long userId;
    private String content;

    private CommunityComment(
            Long id,
            Long postId,
            Long userId,
            String content
    ) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static CommunityComment create(
            Long postId,
            Long userId,
            String content
    ) {

        return new CommunityComment(
                null,
                postId,
                userId,
                content
        );
    }

    public static CommunityComment reconstruct(
            Long id,
            Long postId,
            Long userId,
            String content
    ) {

        return new CommunityComment(
                id,
                postId,
                userId,
                content
        );
    }

    public void update(
            String content
    ) {

        this.content = content;
    }

    public boolean isOwner(
            Long userId
    ) {

        return Objects.equals(
                this.userId,
                userId
        );
    }
}
