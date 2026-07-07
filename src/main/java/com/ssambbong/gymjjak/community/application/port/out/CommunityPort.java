package com.ssambbong.gymjjak.community.application.port.out;

import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityPort {
    Long saveCommunityPost(CommunityPost communityPost);

    Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            Pageable pageable
    );
}
