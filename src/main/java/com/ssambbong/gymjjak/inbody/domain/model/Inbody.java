package com.ssambbong.gymjjak.inbody.domain.model;

import com.ssambbong.gymjjak.inbody.domain.exception.InbodyErrorCode;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyRequiredFieldException;
import com.ssambbong.gymjjak.inbody.domain.exception.InvalidInbodyValueException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Inbody {

    private final Long id;
    private final Long userId;
    private final LocalDate measuredDate;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bodyFatPercentage;
    private BigDecimal skeletalMuscleMass;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private Inbody(
            Long id,
            Long userId,
            LocalDate measuredDate,
            BigDecimal height,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = requireUserId(userId);
        this.measuredDate = requireMeasuredDate(measuredDate);
        this.height = requirePositive(height, "height");
        this.weight = requirePositive(weight, "weight");
        this.bodyFatPercentage = validateOptionalNonNegative(bodyFatPercentage, "bodyFatPercentage");
        this.skeletalMuscleMass = validateOptionalNonNegative(skeletalMuscleMass, "skeletalMuscleMass");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Inbody create(
            Long userId,
            LocalDate measuredDate,
            BigDecimal height,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass
    ) {
        return new Inbody(
                null,
                userId,
                measuredDate,
                height,
                weight,
                bodyFatPercentage,
                skeletalMuscleMass,
                null,
                null
        );
    }

    public static Inbody reconstruct(
            Long id,
            Long userId,
            LocalDate measuredDate,
            BigDecimal height,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Inbody(
                id,
                userId,
                measuredDate,
                height,
                weight,
                bodyFatPercentage,
                skeletalMuscleMass,
                createdAt,
                updatedAt
        );
    }

    public void update(
            BigDecimal height,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass
    ) {
        this.height = requirePositive(height, "height");
        this.weight = requirePositive(weight, "weight");
        this.bodyFatPercentage = validateOptionalNonNegative(bodyFatPercentage, "bodyFatPercentage");
        this.skeletalMuscleMass = validateOptionalNonNegative(skeletalMuscleMass, "skeletalMuscleMass");
    }

    private static Long requireUserId(Long userId) {
        if (userId == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.USER_ID_REQUIRED);
        }
        return userId;
    }

    private static LocalDate requireMeasuredDate(LocalDate measuredDate) {
        if (measuredDate == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.MEASURED_DATE_REQUIRED);
        }
        return measuredDate;
    }

    private static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        if (value == null) {
            if ("height".equals(fieldName)) {
                throw new InbodyRequiredFieldException(InbodyErrorCode.HEIGHT_REQUIRED);
            }
            throw new InbodyRequiredFieldException(InbodyErrorCode.WEIGHT_REQUIRED);
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInbodyValueException(fieldName);
        }
        return value;
    }

    private static BigDecimal validateOptionalNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInbodyValueException(fieldName);
        }
        return value;
    }

}
