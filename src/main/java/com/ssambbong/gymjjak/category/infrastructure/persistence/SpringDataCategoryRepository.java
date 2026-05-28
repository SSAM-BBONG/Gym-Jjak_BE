package com.ssambbong.gymjjak.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// domain/application은 이 인터페이스를 알지 못함
public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByName(String name); // DB에 없어서
}
