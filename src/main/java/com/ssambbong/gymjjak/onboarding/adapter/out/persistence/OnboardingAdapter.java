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
    public void saveOnboardingSurvey(OnboardingSurvey onboardingSurvey) {

        RegionJpaEntity region = regionJpaRepository.findById(onboardingSurvey.getPreferredRegionId())
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.REGION_NOT_FOUND));

        onboardingSurveyJpaRepository.save(
                onboardingMapper.toOnboardingSurveyEntity(
                        onboardingSurvey,
                        region
                )
        );
    }

    @Override
    public Optional<MyOnboardingView> findMyOnboardingByUserId(Long userId) {
        return onboardingSurveyJpaRepository.findMyOnboardingByUserId(userId);
    }
}
