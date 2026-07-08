package com.ssambbong.gymjjak.part.infrastructure.persistence;

import com.ssambbong.gymjjak.part.domain.exception.PartNotFoundException;
import com.ssambbong.gymjjak.part.domain.model.Part;
import com.ssambbong.gymjjak.part.domain.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PartRepositoryAdapter implements PartRepository {

    private final SpringDataPartRepository repository;

    @Override
    public Part save(Part part) {
        PartJpaEntity entity = part.getId() == null
                ? new PartJpaEntity(part.getName())
                : repository.findById(part.getId()).orElseThrow(PartNotFoundException::new);
        entity.changeName(part.getName());
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Part> findById(Long id) {
        return repository.findById(id).map(PartJpaEntity::toDomain);
    }

    @Override
    public List<Part> findAll() {
        return repository.findAll()
                .stream()
                .map(PartJpaEntity::toDomain)
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
    public long countPtCoursesByPartId(Long partId) {
        return repository.countPtCoursesByPartId(partId);
    }

    @Override
    public Map<Long, Long> countPtCoursesByPartIds(List<Long> partIds) {
        if (partIds.isEmpty()) return Map.of();
        return repository.countPtCoursesByPartIds(partIds).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }
}
