package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "organization_trainers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationTrainerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_trainer_id")
    private Long organizationTrainerId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "registered_by", nullable = false)
    private Long registeredBy;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    public OrganizationTrainer toDomain() {
        return OrganizationTrainer.restore(
                organizationTrainerId,
                organizationId,
                trainerProfileId,
                registeredBy,
                registeredAt,
                removedAt
        );
    }
}
