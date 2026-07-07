package com.ssambbong.gymjjak.community.application.service;

import com.ssambbong.gymjjak.community.application.command.CreateCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.port.in.CommunityUseCase;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
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

        return communityPort.saveCommunityPost(communityPost);
    }

    @Override
    public Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            Pageable pageable
    ) {

        return communityPort.findCommunityPosts(
                type,
                pageable
        );
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
}
