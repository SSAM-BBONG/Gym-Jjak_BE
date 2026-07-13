package com.ssambbong.gymjjak.community.application.service;

import com.ssambbong.gymjjak.community.application.command.*;
import com.ssambbong.gymjjak.community.application.port.in.CommunityUseCase;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.exception.CommunityErrorCode;
import com.ssambbong.gymjjak.community.domain.exception.CommunityException;
import com.ssambbong.gymjjak.community.domain.model.CommunityComment;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService implements CommunityUseCase {

    private final CommunityPort communityPort;

    @Override
    public Long createCommunityPost(
            CreateCommunityPostCommand command
    ) {

        log.debug("event=communityPost_create_start userId={}, type={}",
                command.userId(),
                command.type());

        validateCreatePermission(
                command.type(),
                command.role()
        );

        CommunityPost communityPost = CommunityPost.create(
                command.userId(),
                command.type(),
                command.title(),
                command.content()
        );

        Long postId =
                communityPort.saveCommunityPost(
                        communityPost
                );

        log.info("event=communityPost_create_succeed userId={}, postId={}, type={}",
                command.userId(),
                postId,
                command.type());

        return postId;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            String keyword,
            Pageable pageable
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);

        log.debug("event=communityPost_listFind_start type={} keyword={}",
                type == null ? "ALL" : type,
                normalizedKeyword);

        Page<CommunityPostListResult> result =
                communityPort.findCommunityPosts(
                        type,
                        normalizedKeyword,
                        pageable
                );

        log.info("event=communityPost_listFind_succeed type={}, keyword={}, page={}, size={}, totalElements={}",
                type == null ? "ALL" : type,
                normalizedKeyword,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                result.getTotalElements());

        return result;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    @Override
    @Transactional
    public CommunityPostDetailResult findCommunityPostDetail(
            Long userId,
            Long postId,
            Long commentCursorId,
            int commentSize
    ) {

        log.debug("event=communityPost_detailFind userId={}, postId={}",
                userId,
                postId);

        validateCommunityPostExists(postId);

        boolean firstView =
                communityPort.saveViewIfAbsent(
                        postId,
                        userId
                );

        if (firstView) {
            communityPort.increaseViewCount(postId);
        }

        CommunityPostDetailResult result = communityPort.findCommunityPostDetail(
                                postId,
                                userId,
                                commentCursorId,
                                commentSize
                        ).orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));

        log.info("event=communityPost_detailFind userId={}, postId={}, firstView={}, viewCount={}",
                userId,
                postId,
                firstView,
                result.viewCount());

        return result;
    }

    @Override
    public void updateCommunityPost(
            UpdateCommunityPostCommand command
    ) {

        CommunityPost communityPost =
                communityPort
                        .findCommunityPostById(
                                command.postId()
                        )
                        .orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode.COMMUNITY_POST_NOT_FOUND
                                )
                        );

        log.debug(
                "event=communityPost_update_start userId={}, postId={}",
                command.userId(),
                command.postId()
        );

        validateCommunityPostOwner(
                communityPost,
                command.userId()
        );

        communityPost.update(
                command.title(),
                command.content()
        );

        communityPort.updateCommunityPost(
                communityPost
        );

        log.info(
                "event=communityPost_update_succeed userId={}, postId={}",
                command.userId(),
                command.postId()
        );
    }

    @Override
    public void deleteCommunityPost(DeleteCommunityPostCommand command) {

        CommunityPost communityPost = communityPort.findCommunityPostById(command.postId())
                        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));

        validateCommunityPostDeleteOwner(
                communityPost,
                command.userId()
        );

        log.debug("event=communityPost_delete_start userId={}, postId={}",
                command.userId(),
                command.postId()
        );

        communityPort.deleteCommunityPost(command.postId());

        log.info("event=communityPost_delete_succeed userId={}, postId={}",
                command.userId(),
                command.postId()
        );
    }

    @Override
    public Long createCommunityComment(
            CreateCommunityCommentCommand command
    ) {

        validateCommunityPostExists(
                command.postId()
        );

        log.debug("event=communityComment_create_start userId={}, postId={}",
                command.userId(),
                command.postId()
        );

        CommunityComment communityComment =
                CommunityComment.create(
                        command.postId(),
                        command.userId(),
                        command.content()
                );

        Long commentId = communityPort.saveCommunityComment(communityComment);

        log.info("event=communityComment_create_succeed userId={}, postId={}, commentId={}",
                command.userId(),
                command.postId(),
                commentId
        );

        return commentId;
    }

    @Override
    public void updateCommunityComment(
            UpdateCommunityCommentCommand command
    ) {

        CommunityComment communityComment =
                communityPort
                        .findCommunityCommentById(
                                command.commentId()
                        )
                        .orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode.COMMUNITY_COMMENT_NOT_FOUND
                                )
                        );

        validateCommunityPostExists(
                communityComment.getPostId()
        );

        validateCommunityCommentOwner(
                communityComment,
                command.userId()
        );

        log.debug("event=communityComment_update_succeed userId={}, postId={}, commentId={}",
                command.userId(),
                communityComment.getPostId(),
                command.commentId()
        );

        communityComment.update(
                command.content()
        );

        communityPort.updateCommunityComment(
                communityComment
        );

        log.info("event=communityComment_update_succeed userId={}, postId={}, commentId={}",
                command.userId(),
                communityComment.getPostId(),
                command.commentId()
        );
    }

    @Override
    public void deleteCommunityComment(
            DeleteCommunityCommentCommand command
    ) {

        CommunityComment communityComment =
                communityPort
                        .findCommunityCommentById(
                                command.commentId()
                        )
                        .orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode.COMMUNITY_COMMENT_NOT_FOUND
                                )
                        );

        validateCommunityPostExists(
                communityComment.getPostId()
        );

        validateCommunityCommentDeleteOwner(
                communityComment,
                command.userId()
        );
        log.debug("event=communityComment_delete_succeed userId={}, postId={}, commentId={}",
                command.userId(),
                communityComment.getPostId(),
                command.commentId()
        );

        communityPort.deleteCommunityComment(
                command.commentId()
        );

        log.info("event=communityComment_delete_succeed userId={}, postId={}, commentId={}",
                command.userId(),
                communityComment.getPostId(),
                command.commentId()
        );
    }

    @Override
    public void createCommunityPostLike(
            CreateCommunityPostLikeCommand command
    ) {

        validateCommunityPostExists(
                command.postId()
        );

        log.debug("event=communityPost_like_start userId={}, postId={}",
                command.userId(),
                command.postId()
        );

        boolean created =
                communityPort
                        .saveCommunityPostLikeIfAbsent(
                                command.postId(),
                                command.userId()
                        );

        if (!created) {

            throw new CommunityException(
                    CommunityErrorCode
                            .COMMUNITY_POST_LIKE_ALREADY_EXISTS
            );
        }

        log.info("event=communityPost_like_succeed userId={}, postId={}",
                command.userId(),
                command.postId()
        );
    }

    @Override
    public void deleteCommunityPostLike(
            DeleteCommunityPostLikeCommand command
    ) {

        validateCommunityPostExists(
                command.postId()
        );

        log.debug("event=communityPost_likeDelete_start userId={}, postId={}",
                command.userId(),
                command.postId()
        );

        boolean deleted =
                communityPort
                        .deleteCommunityPostLike(
                                command.postId(),
                                command.userId()
                        );

        if (!deleted) {

            throw new CommunityException(
                    CommunityErrorCode
                            .COMMUNITY_POST_LIKE_NOT_FOUND
            );
        }

        log.info("event=communityPost_likeDelete_succeed userId={}, postId={}",
                command.userId(),
                command.postId()
        );
    }

    private void validateCommunityCommentDeleteOwner(
            CommunityComment communityComment,
            Long userId
    ) {

        if (!communityComment.isOwner(userId)) {

            throw new CommunityException(
                    CommunityErrorCode
                            .COMMUNITY_COMMENT_DELETE_FORBIDDEN
            );
        }
    }

    private void validateCommunityCommentOwner(
            CommunityComment communityComment,
            Long userId
    ) {

        if (!communityComment.isOwner(userId)) {

            throw new CommunityException(
                    CommunityErrorCode
                            .COMMUNITY_COMMENT_UPDATE_FORBIDDEN
            );
        }
    }

    private void validateCommunityPostDeleteOwner(CommunityPost communityPost, Long userId) {

        if (!communityPost.isOwner(userId)) {

            throw new CommunityException(
                    CommunityErrorCode.COMMUNITY_POST_DELETE_FORBIDDEN
            );
        }
    }

    private void validateCommunityPostExists(Long postId) {

        if (!communityPort.existsCommunityPost(postId)) {

            throw new CommunityException(
                    CommunityErrorCode.COMMUNITY_POST_NOT_FOUND
            );
        }
    }

    private void validateCreatePermission(
            CommunityPostType type,
            String role
    ) {
        if (type == CommunityPostType.NOTICE
                && !"ADMIN".equals(role)) {

            throw new CommunityException(
                    CommunityErrorCode.NOTICE_WRITE_FORBIDDEN
            );
        }
    }

    private void validateCommunityPostOwner(
            CommunityPost communityPost,
            Long userId
    ) {

        if (!communityPost.isOwner(userId)) {

            throw new CommunityException(
                    CommunityErrorCode.COMMUNITY_POST_UPDATE_FORBIDDEN
            );
        }
    }
}
