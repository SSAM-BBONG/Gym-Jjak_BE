package com.ssambbong.gymjjak.community.application.port.in;

import com.ssambbong.gymjjak.community.application.command.CreateCommunityCommentCommand;
import com.ssambbong.gymjjak.community.application.command.CreateCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.command.DeleteCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.command.UpdateCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityUseCase {

    Long createCommunityPost(CreateCommunityPostCommand command);

    Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            Pageable pageable
    );

    CommunityPostDetailResult findCommunityPostDetail(
            Long userId,
            Long postId,
            Long commentCursorId,
            int commentSize
    );

    void updateCommunityPost(UpdateCommunityPostCommand command);

    void deleteCommunityPost(DeleteCommunityPostCommand command);

    Long createCommunityComment(CreateCommunityCommentCommand command);
}
