package com.ssambbong.gymjjak.calendar.domain.model;

import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;

import java.math.BigDecimal;

public class WorkoutDiarySet {

    private final Long id;
    private final Integer setOrder;
    private final BigDecimal weight;
    private final Integer reps;

    private WorkoutDiarySet(
            Long id,
            Integer setOrder,
            BigDecimal weight,
            Integer reps
    ) {
        this.id = id;
        this.setOrder = validateSetOrder(setOrder);
        this.weight = validateWeight(weight);
        this.reps = validateReps(reps);
    }

    public static WorkoutDiarySet create(
            Integer setOrder,
            BigDecimal weight,
            Integer reps
    ) {
        return new WorkoutDiarySet(null, setOrder, weight, reps);
    }

    private static Integer validateSetOrder(Integer setOrder) {
        if (setOrder == null) {
            throw new CalendarException(CalendarErrorCode.SET_ORDER_REQUIRED);
        }
        if (setOrder <= 0) {
            throw new CalendarException(CalendarErrorCode.INVALID_SET_ORDER);
        }
        return setOrder;
    }

    private static BigDecimal validateWeight(BigDecimal weight) {
        if (weight == null) {
            throw new CalendarException(CalendarErrorCode.WEIGHT_REQUIRED);
        }
        if (weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new CalendarException(CalendarErrorCode.INVALID_WEIGHT);
        }
        return weight;
    }

    private static Integer validateReps(Integer reps) {
        if (reps == null) {
            throw new CalendarException(CalendarErrorCode.REPS_REQUIRED);
        }
        if (reps <= 0) {
            throw new CalendarException(CalendarErrorCode.INVALID_REPS);
        }
        return reps;
    }

    public Long getId() { return id; }
    public Integer getSetOrder() { return setOrder; }
    public BigDecimal getWeight() { return weight; }
    public Integer getReps() { return reps; }
}
