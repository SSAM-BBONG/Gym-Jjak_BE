package com.ssambbong.gymjjak.part.domain.repository;

import com.ssambbong.gymjjak.part.domain.model.Part;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PartRepository {

    Part save(Part part);
    Optional<Part> findById(Long id);
    List<Part> findAll();
    void deleteById(Long id);
    boolean existsByName(String name);
    long countPtCoursesByPartId(Long partId);
    Map<Long, Long> countPtCoursesByPartIds(List<Long> partIds);
}
