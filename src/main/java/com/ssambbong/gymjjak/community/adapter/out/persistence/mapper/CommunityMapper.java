package com.ssambbong.gymjjak.community.adapter.out.persistence.mapper;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityCommentProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostDetailProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostListProjection;
import com.ssambbong.gymjjak.community.application.result.CommunityCommentCursorResult;
import com.ssambbong.gymjjak.community.application.result.CommunityCommentResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CommunityMapper {

    default CommunityPostJpaEntity toEntity(
            CommunityPost communityPost
    ) {

        if (communityPost == null) {
            return null;
        }

        return CommunityPostJpaEntity.create(
                communityPost.getUserId(),
                communityPost.getType(),
                communityPost.getTitle(),
                communityPost.getContent(),
                communityPost.getViewCount()
        );
    }

    default CommunityPost toDomain(
            CommunityPostJpaEntity entity
    ) {

        if (entity == null) {
            return null;
        }

        return CommunityPost.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewCount()
        );
    }

    default CommunityPostListResult toPostListResult(
            CommunityPostListProjection projection
    ) {

        if (projection == null) {
            return null;
        }

        return new CommunityPostListResult(
                projection.getPostId(),
                CommunityPostType.valueOf(
                        projection.getType()
                ),
                projection.getTitle(),
                projection.getContent(),
                projection.getAuthor(),
                projection.getCreatedAt(),
                projection.getViewCount(),
                projection.getLikeCount(),
                projection.getCommentCount()
        );
    }

    default CommunityCommentResult toCommentResult(
            CommunityCommentProjection projection
    ) {

        if (projection == null) {
            return null;
        }

        return new CommunityCommentResult(
                projection.getCommentId(),
                projection.getAuthor(),
                projection.getCreatedAt(),
                projection.getContent(),
                toBoolean(projection.getMine())
        );
    }

    default CommunityPostDetailResult toPostDetailResult(
            CommunityPostDetailProjection projection,
            CommunityCommentCursorResult comments
    ) {

        if (projection == null) {
            return null;
        }

        return new CommunityPostDetailResult(
                projection.getPostId(),
                CommunityPostType.valueOf(
                        projection.getType()
                ),
                projection.getTitle(),
                projection.getContent(),
                projection.getAuthor(),
                projection.getCreatedAt(),
                projection.getViewCount(),
                projection.getLikeCount(),
                projection.getCommentCount(),
                toBoolean(projection.getMine()),
                toBoolean(projection.getLikedByMe()),
                comments
        );
    }

    default boolean toBoolean(Long value) {
        return Long.valueOf(1L).equals(value);
    }
}
