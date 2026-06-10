package com.ssambbong.gymjjak.onboarding.application.service;

import com.ssambbong.gymjjak.onboarding.application.command.RegisterOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.application.port.out.MyOnboardingView;
import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingPort;
import com.ssambbong.gymjjak.onboarding.application.port.out.UserPortFromOnboarding;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingErrorCode;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingException;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService implements OnboardingUsecase {

    private final OnboardingPort onboardingPort;
    private final UserPortFromOnboarding userPortFromOnboarding;

    @Override
    public void register(RegisterOnboardingCommand command) {
        log.info("[onboarding] 온보딩 등록 처리 시작. userId={}", command.userId());

        boolean userExists = userPortFromOnboarding.existsById(command.userId());
        log.info("[onboarding] 사용자 존재 여부 확인. userId={}, exists={}", command.userId(), userExists);

        if (!userPortFromOnboarding.existsById(command.userId())) {
            throw new OnboardingException(OnboardingErrorCode.USER_NOT_FOUND);
        }

        boolean onboardingExists = onboardingPort.existsByUserId(command.userId());
        log.info("[onboarding] 온보딩 존재 여부 확인. userId={}, exists={}", command.userId(), onboardingExists);

        if (onboardingPort.existsByUserId(command.userId())) {
            throw new OnboardingException(OnboardingErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }

        Region region = Region.create(
                command.region().sido(),
                command.region().sigungu(),
                command.region().eupmyeondong(),
                command.region().fullName(),
                command.region().latitude(),
                command.region().longitude()
        );

        log.info("[onboarding] 선호 지역 생성 완료. userId={}, sido={}, sigungu={}, eupmyeondong={}",
                command.userId(),
                command.region().sido(),
                command.region().sigungu(),
                command.region().eupmyeondong());

        Long regionId = onboardingPort.saveRegion(region);

        log.info("[onboarding] 선호 지역 저장 완료. userId={}, regionId={}",
                command.userId(),
                regionId);

        OnboardingSurvey onboardingSurvey = OnboardingSurvey.create(
                command.userId(),
                command.exerciseGoal(),
                command.exercisePeriod(),
                command.exerciseFrequency(),
                command.preferredExercise(),
                regionId,
                command.height(),
                command.weight()
        );

        onboardingPort.saveOnboardingSurvey(onboardingSurvey);

        log.info("[onboarding] 온보딩 설문 저장 완료. userId={}, regionId={}",
                command.userId(),
                regionId);

        userPortFromOnboarding.completeOnboarding(command.userId());
        log.info("[onboarding] 사용자 온보딩 완료 상태 변경 완료. userId={}", command.userId());
    }

    @Override
    public MyOnboardingResult getMyOnboarding(Long userId) {
        MyOnboardingView view = onboardingPort.findMyOnboardingByUserId(userId)
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.ONBOARDING_NOT_FOUND));

        return new MyOnboardingResult(
                view.onboardingId(),
                view.exerciseGoal(),
                view.exercisePeriod(),
                view.exerciseFrequency(),
                view.preferredExercise(),
                new MyOnboardingResult.RegionResult(
                        view.regionId(),
                        view.sido(),
                        view.sigungu(),
                        view.eupmyeondong(),
                        view.fullName(),
                        view.latitude(),
                        view.longitude()
                ),
                view.height(),
                view.weight()
        );
    }
}