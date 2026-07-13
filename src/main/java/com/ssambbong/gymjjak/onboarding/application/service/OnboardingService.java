package com.ssambbong.gymjjak.onboarding.application.service;

import com.ssambbong.gymjjak.onboarding.application.command.RegisterOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.command.UpdateOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.application.port.out.MyOnboardingView;
import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingCacheEvictionPort;
import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingPort;
import com.ssambbong.gymjjak.onboarding.application.port.out.UserPortFromOnboarding;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingErrorCode;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingException;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService implements OnboardingUsecase {

    private final OnboardingPort onboardingPort;
    private final UserPortFromOnboarding userPortFromOnboarding;
    private final OnboardingCacheEvictionPort onboardingCacheEvictionPort;

    @Override
    public void register(RegisterOnboardingCommand command) {
        log.debug("event=onboarding_register_start userId={}", command.userId());

        boolean userExists = userPortFromOnboarding.existsById(command.userId());

        if (!userExists) {
            throw new OnboardingException(OnboardingErrorCode.USER_NOT_FOUND);
        }

        boolean onboardingExists = onboardingPort.existsByUserId(command.userId());

        if (onboardingExists) {
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

        log.info("event=region_save_start userId={}, sido={}, sigungu={}, eupmyeondong={}, fullName={}, latitude={}, longitude={}",
                command.userId(),
                command.region().sido(),
                command.region().sigungu(),
                command.region().eupmyeondong(),
                command.region().fullName(),
                command.region().latitude(),
                command.region().longitude());

        Long regionId = onboardingPort.saveRegion(region);

        log.info("event=region_save_succeed userId={}, regionId={}",
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

        Long onboardingId = onboardingPort.saveOnboardingSurvey(onboardingSurvey);

        log.info("event=onboarding_save_succeed userId={}, onboardingId={}",
                command.userId(),
                onboardingId);

        userPortFromOnboarding.completeOnboarding(command.userId());
        evictMyOnboardingAfterCommit(command.userId());
        log.info("event=onboardingCompleted_update_done userId={}", command.userId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "myOnboarding", key = "#userId", sync = true)
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

    @Override
    public void updateOnboarding(UpdateOnboardingCommand command) {
        log.debug("event=onboarding_update_start userId={}", command.userId());

        boolean userExists = userPortFromOnboarding.existsById(command.userId());

        if (!userExists) {
            throw new OnboardingException(OnboardingErrorCode.USER_NOT_FOUND);
        }

        MyOnboardingView existingOnboarding = onboardingPort.findMyOnboardingByUserId(command.userId())
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.ONBOARDING_NOT_FOUND));

        Long regionId = existingOnboarding.regionId();

        Region region = Region.create(
                command.region().sido(),
                command.region().sigungu(),
                command.region().eupmyeondong(),
                command.region().fullName(),
                command.region().latitude(),
                command.region().longitude()
        );

        log.info("event=region_update_start userId={}, sido={}, sigungu={}, eupmyeondong={}, fullName={}, latitude={}, longitude={}",
                command.userId(),
                command.region().sido(),
                command.region().sigungu(),
                command.region().eupmyeondong(),
                command.region().fullName(),
                command.region().latitude(),
                command.region().longitude());

        onboardingPort.updateRegion(regionId, region);

        log.info("event=region_update_succeed userId={}, regionId={}",
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

        Long onboardingId = onboardingPort.updateOnboardingSurvey(onboardingSurvey);

        log.info("event=onboarding_update_succeed userId={}, onboardingId={}, regionId={}",
                command.userId(),
                onboardingId,
                regionId);
        evictMyOnboardingAfterCommit(command.userId());

    }

    private void evictMyOnboardingAfterCommit(Long userId) {
        evictAfterCommit(() -> onboardingCacheEvictionPort.evictMyOnboarding(userId));
    }

    private void evictAfterCommit(Runnable eviction) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            eviction.run();
                        }
                    }
            );
            return;
        }
        eviction.run();
    }
}
