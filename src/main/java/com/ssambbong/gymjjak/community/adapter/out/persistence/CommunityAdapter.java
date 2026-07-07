package com.ssambbong.gymjjak.community.adapter.out.persistence;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostDetailProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityRepository;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.community.application.result.CommunityCommentResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommunityAdapter implements CommunityPort {

    private final SpringDataCommunityRepository springDataCommunityRepository;

    @Override
    public Long saveCommunityPost(CommunityPost communityPost) {

        CommunityPostJpaEntity savedCommunityPost = springDataCommunityRepository.save(CommunityPostJpaEntity.from(communityPost));

        return savedCommunityPost.getId();
    }

    @Override
    public Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            Pageable pageable
    ) {

        String typeValue = type == null
                ? null
                : type.name();

        return springDataCommunityRepository
                .findCommunityPosts(
                        typeValue,
                        pageable
                )
                .map(projection ->
                        new CommunityPostListResult(
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
                        )
                );
    }

    @Override
    public boolean saveViewIfAbsent(
            Long postId,
            Long userId
    ) {

        int insertedRowCount =
                springDataCommunityRepository
                        .insertViewIfAbsent(
                                postId,
                                userId
                        );

        return insertedRowCount == 1;
    }

    @Override
    public void increaseViewCount(Long postId) {

        springDataCommunityRepository
                .increaseViewCount(postId);
    }

    @Override
    public boolean existsCommunityPost(Long postId) {

        return springDataCommunityRepository
                .existsByIdAndDeletedAtIsNull(postId);
    }

    @Override
    public Optional<CommunityPostDetailResult> findCommunityPostDetail(
            Long postId,
            Long userId
    ) {

        Optional<CommunityPostDetailProjection> postProjection =
                springDataCommunityRepository
                        .findCommunityPostDetail(
                                postId,
                                userId
                        );

        if (postProjection.isEmpty()) {
            return Optional.empty();
        }

        List<CommunityCommentResult> comments =
                springDataCommunityRepository
                        .findCommunityComments(
                                postId,
                                userId
                        )
                        .stream()
                        .map(projection ->
                                new CommunityCommentResult(
                                        projection.getCommentId(),
                                        projection.getAuthor(),
                                        projection.getCreatedAt(),
                                        projection.getContent(),
                                        toBoolean(projection.getMine())
                                )
                        )
                        .toList();

        CommunityPostDetailProjection post =
                postProjection.get();

        return Optional.of(
                new CommunityPostDetailResult(
                        post.getPostId(),
                        CommunityPostType.valueOf(
                                post.getType()
                        ),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor(),
                        post.getCreatedAt(),
                        post.getViewCount(),
                        post.getLikeCount(),
                        post.getCommentCount(),
                        toBoolean(post.getMine()),
                        toBoolean(post.getLikedByMe()),
                        comments
                )
        );
    }

    private boolean toBoolean(Long value) {
        return value != null && value == 1L;
    }
}
