package com.ssambbong.gymjjak.community.adapter.out.persistence.repository;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCommunityCommentRepository
        extends JpaRepository<CommunityCommentJpaEntity, Long> {
}
