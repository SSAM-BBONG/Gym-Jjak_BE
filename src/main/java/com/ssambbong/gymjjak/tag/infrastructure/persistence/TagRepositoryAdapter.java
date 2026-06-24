package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import com.ssambbong.gymjjak.tag.domain.model.Tag;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepository {

    private final SpringDataTagRepository repository;

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity entity = tag.getId() == null
                ? new TagJpaEntity(tag.getName())
                : repository.findById(tag.getId()).orElseThrow();
        entity.changeName(tag.getName());
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return repository.findById(id).map(TagJpaEntity::toDomain);
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAllByDeletedAtIsNull()
                .stream()
                .map(TagJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            repository.save(entity);
        });
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameAndDeletedAtIsNull(name);
    }

    @Override
    public long countPtCoursesByTagId(Long tagId) {
        return repository.countPtCoursesByTagId(tagId);
    }
}
