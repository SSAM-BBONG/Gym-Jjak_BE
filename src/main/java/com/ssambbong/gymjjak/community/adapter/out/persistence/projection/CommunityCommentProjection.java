package com.ssambbong.gymjjak.community.adapter.out.persistence.projection;

import java.time.LocalDateTime;

public interface CommunityCommentProjection {

    Long getCommentId();

    String getAuthor();

    LocalDateTime getCreatedAt();

    String getContent();

    Long getMine();
}
