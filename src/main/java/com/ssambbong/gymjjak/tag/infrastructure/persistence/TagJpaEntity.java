package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.tag.domain.model.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tags")
public class TagJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    public TagJpaEntity(String name) {
        this.name = name;
    }

    public Tag toDomain() {
        return Tag.restore(id, name, getCreatedAt());
    }

    public void changeName(String name) {
        this.name = name;
    }
}
