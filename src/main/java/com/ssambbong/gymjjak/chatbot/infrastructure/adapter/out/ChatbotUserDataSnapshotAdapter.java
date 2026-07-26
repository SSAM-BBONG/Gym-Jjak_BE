package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out;

import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshotPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutData;
import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyQueryUseCase;
import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingException;
import com.ssambbong.gymjjak.onboarding.domain.exception.OnboardingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ChatbotUserDataSnapshotAdapter implements ChatbotUserDataSnapshotPort {
    private static final int WORKOUT_HISTORY_DAYS = 28;

    private final OnboardingUsecase onboardingUsecase;
    private final WorkoutDiaryPort workoutDiaryPort;
    private final InbodyQueryUseCase inbodyQueryUseCase;

    @Override
    // Loads only the personal data required for the current chatbot request.
    public ChatbotUserDataSnapshot load(Long userId) {
        LocalDate endDate = LocalDate.now().plusDays(1);
        ChatbotUserDataSnapshot.Onboarding onboarding = loadOnboarding(userId);

        // Converts calendar entities to an AI-specific snapshot before aggregation.
        var workouts = workoutDiaryPort.findDiariesByUserIdAndPeriod(
                        userId, endDate.minusDays(WORKOUT_HISTORY_DAYS), endDate)
                .stream()
                .map(diary -> new ChatbotUserDataSnapshot.Workout(
                        diary.date(), diary.part().name(), diary.exercise(),
                        diary.sets().stream().map(set -> new ChatbotUserDataSnapshot.WorkoutSet(
                                set.setOrder(), set.weight(), set.reps())).toList()))
                .toList();
        var inbodies = inbodyQueryUseCase.getInbodyList(new GetInbodyListQuery(userId, null, null))
                .inbodies().stream()
                .map(inbody -> new ChatbotUserDataSnapshot.Inbody(
                        inbody.measuredDate(), inbody.weight(), inbody.bodyFatPercentage(), inbody.skeletalMuscleMass()))
                .toList();

        // Sends only recent details while retaining the full 28-day trend as a summary.
        return new ChatbotUserDataSnapshot(
                onboarding,
                ChatbotWorkoutData.from(workouts, 30, WORKOUT_HISTORY_DAYS),
                inbodies
        );
    }

    // Treats a missing onboarding record as optional but propagates all other failures.
    private ChatbotUserDataSnapshot.Onboarding loadOnboarding(Long userId) {
        try {
            var onboarding = onboardingUsecase.getMyOnboarding(userId);
            return new ChatbotUserDataSnapshot.Onboarding(
                    onboarding.exerciseGoal(), onboarding.exercisePeriod(),
                    onboarding.exerciseFrequency(), onboarding.preferredExercise());
        } catch (OnboardingException exception) {
            if (exception.getErrorCode() == OnboardingErrorCode.ONBOARDING_NOT_FOUND) {
                return null;
            }
            throw exception;
        }
    }
}
