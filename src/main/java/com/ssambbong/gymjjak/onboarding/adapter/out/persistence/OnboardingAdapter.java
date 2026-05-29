package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingPort;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnboardingAdapter implements OnboardingPort {

    private final RegionJpaRepository regionJpaRepository;
    private final OnboardingSurveyJpaRepository onboardingSurveyJpaRepository;
    private final SpringDataUserRepository springDataUserRepository;
    private final OnboardingMapper onboardingMapper;

    @Override
    public boolean existsByUserId(Long userId) {
        return onboardingSurveyJpaRepository.existsByUserId(userId);
    }

    @Override
    public Long saveRegion(Region region) {
        return regionJpaRepository.findByFullNameAndLatitudeAndLongitude(
                        region.getFullName(),
                        region.getLatitude(),
                        region.getLongitude()
                )
                .map(RegionJpaEntity::getId)
                .orElseGet(() -> {
                    RegionJpaEntity savedRegion = regionJpaRepository.save(
                            onboardingMapper.toRegionEntity(region)
                    );

                    return savedRegion.getId();
                });
    }

    @Override
    public void saveOnboardingSurvey(OnboardingSurvey onboardingSurvey) {
        onboardingSurveyJpaRepository.save(
                onboardingMapper.toOnboardingSurveyEntity(onboardingSurvey)
        );
    }

    @Override
    public void completeUserOnboarding(Long userId) {
        UserJpaEntity user = springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.completeOnboarding();
    }
}
