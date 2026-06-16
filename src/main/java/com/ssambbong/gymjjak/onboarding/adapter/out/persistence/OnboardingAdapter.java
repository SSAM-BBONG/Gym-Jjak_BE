package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.onboarding.application.port.out.MyOnboardingView;
import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingPort;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingErrorCode;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingException;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OnboardingAdapter implements OnboardingPort {

    private final RegionJpaRepository regionJpaRepository;
    private final OnboardingSurveyJpaRepository onboardingSurveyJpaRepository;
    private final OnboardingMapper onboardingMapper;

    @Override
    public boolean existsByUserId(Long userId) {
        return onboardingSurveyJpaRepository.existsByUserId(userId);
    }

    @Override
    public Long saveRegion(Region region) {
        RegionJpaEntity savedRegion = regionJpaRepository.save(
                onboardingMapper.toRegionEntity(region)
        );

        return savedRegion.getId();
    }

    @Override
    public Long saveOnboardingSurvey(OnboardingSurvey onboardingSurvey) {

        RegionJpaEntity region = regionJpaRepository.findById(onboardingSurvey.getPreferredRegionId())
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.REGION_NOT_FOUND));

        OnboardingSurveyJpaEntity savedOnboardingSurvey =
                onboardingSurveyJpaRepository.save(
                        onboardingMapper.toOnboardingSurveyEntity(
                                onboardingSurvey,
                                region
                        )
                );

        return savedOnboardingSurvey.getId();

    }

    @Override
    public Optional<MyOnboardingView> findMyOnboardingByUserId(Long userId) {
        return onboardingSurveyJpaRepository.findMyOnboardingByUserId(userId);
    }

    @Override
    public void updateRegion(Long regionId, Region region) {
        RegionJpaEntity regionEntity = regionJpaRepository.findById(regionId)
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.REGION_NOT_FOUND));

        regionEntity.update(
                region.getSido(),
                region.getSigungu(),
                region.getEupmyeondong(),
                region.getFullName(),
                region.getLatitude(),
                region.getLongitude()
        );
    }

    @Override
    public Long updateOnboardingSurvey(OnboardingSurvey onboardingSurvey) {
        OnboardingSurveyJpaEntity onboardingSurveyEntity =
                onboardingSurveyJpaRepository.findByUserId(onboardingSurvey.getUserId())
                        .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.ONBOARDING_NOT_FOUND));

        RegionJpaEntity region = regionJpaRepository.findById(onboardingSurvey.getPreferredRegionId())
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.REGION_NOT_FOUND));

        onboardingSurveyEntity.update(
                onboardingSurvey.getExerciseGoal(),
                onboardingSurvey.getExercisePeriod(),
                onboardingSurvey.getExerciseFrequency(),
                onboardingSurvey.getPreferredExercise(),
                region,
                onboardingSurvey.getHeight(),
                onboardingSurvey.getWeight()
        );

        return onboardingSurveyEntity.getId();
    }
}
