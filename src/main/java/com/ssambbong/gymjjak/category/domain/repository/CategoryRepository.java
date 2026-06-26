package com.ssambbong.gymjjak.category.domain.repository;

import com.ssambbong.gymjjak.category.domain.model.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    // 등록, 수정
    Category save(Category category);
    // 단건 조회
    Optional<Category> findById(Long id);
    // 목록 조회
    List<Category> findAll();
    // 삭제
    void deleteById(Long id);
    // 중복 이름 확인
    boolean existsByName(String name);

    // 카테고리별 PT 강습 사용 개수
    long countPtCoursesByCategoryId(Long categoryId);
}
