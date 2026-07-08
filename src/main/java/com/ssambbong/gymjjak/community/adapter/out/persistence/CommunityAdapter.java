package com.ssambbong.gymjjak.community.adapter.out.persistence;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.mapper.CommunityMapper;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityCommentProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.projection.CommunityPostDetailProjection;
import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityRepository;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.community.application.result.CommunityCommentCursorResult;
import com.ssambbong.gymjjak.community.application.result.CommunityCommentResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.exception.CommunityErrorCode;
import com.ssambbong.gymjjak.community.domain.exception.CommunityException;
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
    private final CommunityMapper communityMapper;

    @Override
    public Long saveCommunityPost(
            CommunityPost communityPost
    ) {

        CommunityPostJpaEntity entity =
                communityMapper.toEntity(
                        communityPost
                );

        CommunityPostJpaEntity savedCommunityPost =
                springDataCommunityRepository.save(
                        entity
                );

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
                .map(
                        communityMapper::toPostListResult
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
            Long userId,
            Long commentCursorId,
            int commentSize
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

        List<CommunityCommentProjection> commentProjections =
                springDataCommunityRepository
                        .findCommunityCommentsByCursor(
                                postId,
                                userId,
                                commentCursorId,
                                commentSize + 1
                        );

        boolean hasNext =
                commentProjections.size() > commentSize;

        List<CommunityCommentResult> comments =
                commentProjections
                        .stream()
                        .limit(commentSize)
                        .map(
                                communityMapper::toCommentResult
                        )
                        .toList();

        Long nextCursorId =
                hasNext && !comments.isEmpty()
                        ? comments.get(
                        comments.size() - 1
                ).commentId()
                        : null;

        CommunityCommentCursorResult commentCursorResult =
                new CommunityCommentCursorResult(
                        comments,
                        nextCursorId,
                        hasNext
                );

        return Optional.of(
                communityMapper
                        .toPostDetailResult(
                                postProjection.get(),
                                commentCursorResult
                        )
        );
    }

    private boolean toBoolean(Long value) {
        return value != null && value == 1L;
    }

    @Override
    public Optional<CommunityPost> findCommunityPostById(
            Long postId
    ) {

        return springDataCommunityRepository
                .findByIdAndDeletedAtIsNull(
                        postId
                )
                .map(
                        communityMapper::toDomain
                );
    }

    @Override
    public void updateCommunityPost(
            CommunityPost communityPost
    ) {

        int updatedRowCount =
                springDataCommunityRepository
                        .updateCommunityPost(
                                communityPost.getId(),
                                communityPost.getTitle(),
                                communityPost.getContent()
                        );

        if (updatedRowCount == 0) {

            throw new CommunityException(
                    CommunityErrorCode.COMMUNITY_POST_NOT_FOUND
            );
        }
    }
}
