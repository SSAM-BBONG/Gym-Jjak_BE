package com.ssambbong.gymjjak.category.infrastructure.persistence;

import com.ssambbong.gymjjak.category.domain.model.Category;
import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
public class CategoryJpaEntity extends BaseCreatedUpdatedEntity {

    @Id // PK 컬럼 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50) // NOT NULL, UNIQUE, VARCHAR(50)
    private String name;

    // 카테고리 등록 요청을 adapter에서 새 카테고리 저장할 때 사용
    public CategoryJpaEntity(String name) {
        this.name = name;
    }

    // 조회. DB에서 꺼낸 JpaEntity -> domain Model 변환 (서비스는 JpaEntity 몰라야 하므로)
    public Category toDomain() {
        return Category.restore(id, name, getCreatedAt());
    }

    // 카테고리 이름 수정. 도메인 행위가 아니라 DB 저장 상태 변경용
    public void changeName(String name) {
        this.name = name;
    }
}
