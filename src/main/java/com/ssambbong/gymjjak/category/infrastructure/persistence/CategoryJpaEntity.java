package com.ssambbong.gymjjak.category.infrastructure.persistence;

import com.ssambbong.gymjjak.category.domain.model.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
// created_at, updated_at 자동 관리를 위한 JPA Auditing 활성화
@EntityListeners(AuditingEntityListener.class)
public class CategoryJpaEntity {

    @Id // PK 컬럼 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50) // NOT NULL, UNIQUE, VARCHAR(50)
    private String name;

    @CreatedDate // 엔티티 최초 저장 시 자동으로 현재 시간 입력
    @Column(name = "created_at", nullable = false, updatable = false) // updatable=false → 최초 저장 후 변경 불가
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 수정 시 자동으로 현재 시간 입력
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at") // 소프트 딜리트용. null이면 삭제 안 된 것
    private LocalDateTime deletedAt;

    // 카테고리 등록 요청을 adapter에서 새 카테고리 저장할 때 사용
    public CategoryJpaEntity(String name) {
        this.name = name;
    }

    // 조회. DB에서 꺼낸 JpaEntity -> domain Model 변환 (서비스는 JpaEntity 몰라야 하므로)
    public Category toDomain() {
        return Category.restore(id, name);
    }

    // 카테고리 이름 수정. 도메인 행위가 아니라 DB 저장 상태 변경용
    public void changeName(String name) {
        this.name = name;
    }
}
