package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import com.ssambbong.gymjjak.tag.domain.exception.TagNotFoundException;
import com.ssambbong.gymjjak.tag.domain.model.Tag;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepository {

    private final SpringDataTagRepository repository;

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity entity = tag.getId() == null
                ? new TagJpaEntity(tag.getName())
                : repository.findById(tag.getId()).orElseThrow(TagNotFoundException::new);
        entity.changeName(tag.getName());
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return repository.findById(id).map(TagJpaEntity::toDomain);
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAll()
                .stream()
                .map(TagJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public long countPtCoursesByTagId(Long tagId) {
        return repository.countPtCoursesByTagId(tagId);
    }

    @Override
    public Map<Long, Long> countPtCoursesByTagIds(List<Long> tagIds) {
        if (tagIds.isEmpty()) return Map.of();
        return repository.countPtCoursesByTagIds(tagIds).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }
}
