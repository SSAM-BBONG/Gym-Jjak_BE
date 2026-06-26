package com.ssambbong.gymjjak.tag.domain.repository;

import com.ssambbong.gymjjak.tag.domain.model.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagRepository {

    Tag save(Tag tag);
    Optional<Tag> findById(Long id);
    List<Tag> findAll();
    void deleteById(Long id);
    boolean existsByName(String name);
    long countPtCoursesByTagId(Long tagId);
    Map<Long, Long> countPtCoursesByTagIds(List<Long> tagIds);
    int softDeleteIfNotInUse(Long id);

    // 소프트딜리트된 지 threshold 초과한 태그 ID 배치 조회
    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    // 하드딜리트
    int hardDeleteByIds(List<Long> ids);
}
