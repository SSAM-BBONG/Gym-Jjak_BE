package com.ssambbong.gymjjak.inbody.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum BmiStatus {

    UNDERWEIGHT("저체중"),
    NORMAL("정상"),
    OVERWEIGHT("과체중"),
    OBESE("비만");

    private static final BigDecimal NORMAL_MIN = new BigDecimal("18.5");
    private static final BigDecimal OVERWEIGHT_MIN = new BigDecimal("23.0");
    private static final BigDecimal OBESE_MIN = new BigDecimal("25.0");

    private final String description;

    public static BmiStatus from(BigDecimal bmi) {
        if (bmi.compareTo(NORMAL_MIN) < 0) {
            return UNDERWEIGHT;
        }
        if (bmi.compareTo(OVERWEIGHT_MIN) < 0) {
            return NORMAL;
        }
        if (bmi.compareTo(OBESE_MIN) < 0) {
            return OVERWEIGHT;
        }
        return OBESE;
    }
}
