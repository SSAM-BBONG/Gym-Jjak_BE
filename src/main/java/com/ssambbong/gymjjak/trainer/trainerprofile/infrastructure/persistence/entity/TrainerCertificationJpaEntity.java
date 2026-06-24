package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trainer_certifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerCertificationJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_certification_id")
    private Long trainerCertificationId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "file_id")
    private Long fileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "certification_type", nullable = false, length = 30)
    private TrainerCertificationType certificationType;

    @Builder
    private TrainerCertificationJpaEntity(
            Long trainerCertificationId,
            Long trainerProfileId,
            String name,
            Long fileId,
            TrainerCertificationType certificationType
    ) {
        this.trainerCertificationId = trainerCertificationId;
        this.trainerProfileId = trainerProfileId;
        this.name = name;
        this.fileId = fileId;
        this.certificationType = certificationType;
    }
}
