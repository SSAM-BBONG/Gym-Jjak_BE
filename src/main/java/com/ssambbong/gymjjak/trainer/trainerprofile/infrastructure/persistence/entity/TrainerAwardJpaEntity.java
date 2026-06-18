package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trainer_awards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerAwardJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_award_id")
    private Long trainerAwardId;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Builder
    private TrainerAwardJpaEntity(
            Long trainerAwardId,
            Long trainerProfileId,
            String name
    ) {
        this.trainerAwardId = trainerAwardId;
        this.trainerProfileId = trainerProfileId;
        this.name = name;
    }
}
