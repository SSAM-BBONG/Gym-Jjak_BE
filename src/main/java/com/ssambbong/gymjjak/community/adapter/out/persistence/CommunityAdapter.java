package com.ssambbong.gymjjak.community.adapter.out.persistence;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityPostJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityRepository;
import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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
}
