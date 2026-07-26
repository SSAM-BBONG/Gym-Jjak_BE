package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// FastAPI에 전달할 최근 상세 운동과 28일 전체 운동 경향을 함께 보관합니다.
public record ChatbotWorkoutData(
        List<ChatbotUserDataSnapshot.Workout> recentWorkouts,
        Summary summary
) {
    // 최근 상세 운동 개수와 전체 집계 기간을 제한해 payload 크기를 일정하게 유지합니다.
    public static ChatbotWorkoutData from(
            List<ChatbotUserDataSnapshot.Workout> workouts,
            int recentWorkoutLimit,
            int periodDays
    ) {
        LocalDate startDate = LocalDate.now().minusDays(periodDays - 1L);

        // 조회 범위 밖 데이터는 요약·상세 양쪽에서 제외합니다.
        List<ChatbotUserDataSnapshot.Workout> periodWorkouts = workouts.stream()
                .filter(workout -> !workout.diaryDate().isBefore(startDate))
                .toList();

        // 최신 순으로 자른 뒤 화면과 분석이 일관되도록 날짜 오름차순으로 다시 정렬합니다.
        List<ChatbotUserDataSnapshot.Workout> recentWorkouts = periodWorkouts.stream()
                .sorted(Comparator.comparing(ChatbotUserDataSnapshot.Workout::diaryDate).reversed())
                .limit(recentWorkoutLimit)
                .sorted(Comparator.comparing(ChatbotUserDataSnapshot.Workout::diaryDate))
                .toList();

        Map<String, Integer> partSessionCounts = new LinkedHashMap<>();
        Map<String, BigDecimal> partTotalVolumeKg = new LinkedHashMap<>();
        for (ChatbotUserDataSnapshot.Workout workout : periodWorkouts) {
            // 운동일지 한 건을 해당 부위의 세션 한 번으로 집계합니다.
            partSessionCounts.merge(workout.part(), 1, Integer::sum);

            // 세트별 중량×횟수를 합산해 결정론적인 부위별 총 볼륨을 계산합니다.
            BigDecimal volume = workout.sets().stream()
                    .map(set -> set.weight().multiply(BigDecimal.valueOf(set.reps())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            partTotalVolumeKg.merge(workout.part(), volume, BigDecimal::add);
        }

        int workoutDays = (int) periodWorkouts.stream()
                .map(ChatbotUserDataSnapshot.Workout::diaryDate)
                .distinct()
                .count();
        return new ChatbotWorkoutData(recentWorkouts,
                new Summary(periodDays, workoutDays, Map.copyOf(partSessionCounts), Map.copyOf(partTotalVolumeKg)));
    }

    public record Summary(
            int periodDays,
            int workoutDays,
            Map<String, Integer> partSessionCounts,
            Map<String, BigDecimal> partTotalVolumeKg
    ) {
    }
}
