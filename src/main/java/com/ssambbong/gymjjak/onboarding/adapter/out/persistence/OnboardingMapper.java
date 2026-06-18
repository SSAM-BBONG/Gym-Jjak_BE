package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(config = MapStructConfig.class)
public interface OnboardingMapper {

    RegionJpaEntity toRegionEntity(Region region);

    default Region toRegionDomain(RegionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Region.reconstruct(
                entity.getId(),
                entity.getSido(),
                entity.getSigungu(),
                entity.getEupmyeondong(),
                entity.getFullName(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }

    @Mapping(target = "id", source = "onboardingSurvey.id")
    @Mapping(target = "preferredRegion", source = "regionEntity")
    OnboardingSurveyJpaEntity toOnboardingSurveyEntity(
            OnboardingSurvey onboardingSurvey,
            RegionJpaEntity regionEntity
    );

    default OnboardingSurvey toOnboardingSurveyDomain(OnboardingSurveyJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return OnboardingSurvey.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getExerciseGoal(),
                entity.getExercisePeriod(),
                entity.getExerciseFrequency(),
                entity.getPreferredExercise(),
                entity.getPreferredRegion().getId(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
