package com.ssambbong.gymjjak.tag.domain.repository;

import com.ssambbong.gymjjak.tag.domain.model.Tag;

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
}
