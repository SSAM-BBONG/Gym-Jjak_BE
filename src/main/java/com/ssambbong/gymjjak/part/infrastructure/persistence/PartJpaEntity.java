package com.ssambbong.gymjjak.part.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.part.domain.model.Part;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "parts")
public class PartJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    public PartJpaEntity(String name) {
        this.name = name;
    }

    public Part toDomain() {
        return Part.restore(id, name, getCreatedAt());
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.name = name;
    }
}
