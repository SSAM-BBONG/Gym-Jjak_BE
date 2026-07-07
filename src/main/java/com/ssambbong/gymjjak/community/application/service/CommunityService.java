package com.ssambbong.gymjjak.community.application.service;

import com.ssambbong.gymjjak.community.application.command.CreateCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.command.UpdateCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.port.in.CommunityUseCase;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.exception.CommunityErrorCode;
import com.ssambbong.gymjjak.community.domain.exception.CommunityException;
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
            Pageable pageable
    ) {
        log.debug("event=communityPost_listFind_start type={}",
                type == null ? "ALL" : type);

        Page<CommunityPostListResult> result =
                communityPort.findCommunityPosts(
                        type,
                        pageable
                );

        log.info("event=communityPost_listFind_succeed type={}, page={}, size={}, totalElements={}",
                type == null ? "ALL" : type,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                result.getTotalElements());

        return result;
    }

    @Override
    @Transactional
    public CommunityPostDetailResult findCommunityPostDetail(
            Long userId,
            Long postId
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

        CommunityPostDetailResult result =
                communityPort
                        .findCommunityPostDetail(
                                postId,
                                userId
                        )
                        .orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode.COMMUNITY_POST_NOT_FOUND
                                )
                        );

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
