package com.ssambbong.gymjjak.calendar.domain.model;

import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkoutDiary {

    private final Long id;
    private final Long userId;
    private final PartType part;
    private final String exercise;
    private final LocalDate diaryDate;
    private final List<WorkoutDiarySet> sets;

    private WorkoutDiary(
            Long id,
            Long userId,
            LocalDate diaryDate,
            PartType part,
            String exercise,
            List<WorkoutDiarySet> sets
    ) {
        this.id = id;
        this.userId = validateUserId(userId);
        this.diaryDate = validateDiaryDate(diaryDate);
        this.part = validatePart(part);
        this.exercise = validateExercise(exercise);
        this.sets = validateSets(sets);
    }

    public static WorkoutDiary create(
            Long userId,
            LocalDate diaryDate,
            PartType part,
            String exercise,
            List<WorkoutDiarySet> sets
    ) {
        return new WorkoutDiary(null, userId, diaryDate, part, exercise, sets);
    }

    private static Long validateUserId(Long userId) {
        if (userId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }
        return userId;
    }

    private static PartType validatePart(PartType part) {
        if (part == null) {
            throw new CalendarException(CalendarErrorCode.PART_REQUIRED);
        }
        return part;
    }

    private static String validateExercise(String exercise) {
        if (exercise == null || exercise.isBlank()) {
            throw new CalendarException(CalendarErrorCode.EXERCISE_REQUIRED);
        }
        String trimmedExercise = exercise.trim();
        if (trimmedExercise.length() > 100) {
            throw new CalendarException(CalendarErrorCode.EXERCISE_TOO_LONG);
        }
        return trimmedExercise;
    }

    private static LocalDate validateDiaryDate(LocalDate diaryDate) {
        if (diaryDate == null) {
            throw new CalendarException(CalendarErrorCode.DIARY_DATE_REQUIRED);
        }
        return diaryDate;
    }

    private static List<WorkoutDiarySet> validateSets(List<WorkoutDiarySet> sets) {
        if (sets == null || sets.isEmpty()) {
            throw new CalendarException(CalendarErrorCode.SETS_REQUIRED);
        }

        Set<Integer> setOrders = new HashSet<>();
        for (WorkoutDiarySet set : sets) {
            if (set == null || !setOrders.add(set.getSetOrder())) {
                throw new CalendarException(CalendarErrorCode.DUPLICATE_SET_ORDER);
            }
        }
        return List.copyOf(sets);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public PartType getPart() { return part; }
    public String getExercise() { return exercise; }
    public LocalDate getDiaryDate() { return diaryDate; }
    public List<WorkoutDiarySet> getSets() { return sets; }
}
