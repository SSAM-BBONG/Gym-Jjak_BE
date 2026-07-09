package com.ssambbong.gymjjak.community.adapter.out.persistence.projection;

import java.time.LocalDateTime;

public interface CommunityPostListProjection {

    Long getPostId();

    String getType();

    String getTitle();

    String getContent();

    String getAuthor();

    LocalDateTime getCreatedAt();

    Long getViewCount();

    Long getLikeCount();

    Long getCommentCount();
}
