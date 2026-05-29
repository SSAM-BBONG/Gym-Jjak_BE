package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import org.springframework.stereotype.Component;

@Component
public class OnboardingMapper {

    public RegionJpaEntity toRegionEntity(Region region) {
        return new RegionJpaEntity(
                region.getId(),
                region.getSido(),
                region.getSigungu(),
                region.getEupmyeondong(),
                region.getFullName(),
                region.getLatitude(),
                region.getLongitude()
        );
    }

    public Region toRegionDomain(RegionJpaEntity entity) {
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

    public OnboardingSurveyJpaEntity toOnboardingSurveyEntity(OnboardingSurvey onboardingSurvey) {
        return new OnboardingSurveyJpaEntity(
                onboardingSurvey.getId(),
                onboardingSurvey.getUserId(),
                onboardingSurvey.getExerciseGoal(),
                onboardingSurvey.getExercisePeriod(),
                onboardingSurvey.getExerciseFrequency(),
                onboardingSurvey.getPreferredExercise(),
                onboardingSurvey.getPreferredRegionId(),
                onboardingSurvey.getHeight(),
                onboardingSurvey.getWeight()
        );
    }

    public OnboardingSurvey toOnboardingSurveyDomain(OnboardingSurveyJpaEntity entity) {
        return OnboardingSurvey.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getExerciseGoal(),
                entity.getExercisePeriod(),
                entity.getExerciseFrequency(),
                entity.getPreferredExercise(),
                entity.getPreferredRegionId(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
