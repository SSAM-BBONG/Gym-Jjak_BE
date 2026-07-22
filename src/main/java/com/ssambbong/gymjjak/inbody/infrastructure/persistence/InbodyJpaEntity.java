package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "inbody")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InbodyJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbody_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "measured_date", nullable = false)
    private LocalDate measuredDate;

    @Column(name = "height", nullable = false, precision = 5, scale = 2)
    private BigDecimal height;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "body_fat_percentage", precision = 5, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(name = "skeletal_muscle_mass", precision = 5, scale = 2)
    private BigDecimal skeletalMuscleMass;

    @Column(name = "bmr", precision = 6, scale = 2)
    private BigDecimal bmr;

    @Builder
    private InbodyJpaEntity(
            Long id,
            Long userId,
            LocalDate measuredDate,
            BigDecimal height,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass,
            BigDecimal bmr
    ) {
        this.id = id;
        this.userId = userId;
        this.measuredDate = measuredDate;
        this.height = height;
        this.weight = weight;
        this.bodyFatPercentage = bodyFatPercentage;
        this.skeletalMuscleMass = skeletalMuscleMass;
        this.bmr = bmr;
    }

    public void update(Inbody inbody) {
        this.height = inbody.getHeight();
        this.weight = inbody.getWeight();
        this.bodyFatPercentage = inbody.getBodyFatPercentage();
        this.skeletalMuscleMass = inbody.getSkeletalMuscleMass();
        this.bmr = inbody.getBmr();
    }
}
