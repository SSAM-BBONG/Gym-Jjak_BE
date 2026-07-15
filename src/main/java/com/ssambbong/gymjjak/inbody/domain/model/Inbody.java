package com.ssambbong.gymjjak.inbody.domain.model;

import com.ssambbong.gymjjak.inbody.domain.exception.InbodyErrorCode;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyRequiredFieldException;
import com.ssambbong.gymjjak.inbody.domain.exception.InvalidInbodyValueException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Inbody {

    private static final int BMI_SCALE = 1;

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
        BigDecimal validatedHeight = requirePositive(height, "height");
        BigDecimal validatedWeight = requirePositive(weight, "weight");
        BigDecimal validatedBodyFatPercentage =
                validateOptionalNonNegative(bodyFatPercentage, "bodyFatPercentage");
        BigDecimal validatedSkeletalMuscleMass =
                validateOptionalNonNegative(skeletalMuscleMass, "skeletalMuscleMass");

        this.height = validatedHeight;
        this.weight = validatedWeight;
        this.bodyFatPercentage = validatedBodyFatPercentage;
        this.skeletalMuscleMass = validatedSkeletalMuscleMass;
    }

    // 키와 몸무게를 기준으로 BMI 계산
    public BigDecimal calculateBmi() {
        // bmi 계산 시, m 단위기 때문에서 소수점 왼쪽으로 2칸 이동
        BigDecimal heightInMeter = height.movePointLeft(2);

        // 몸무게 / 키 제곱, 소수점 1자리, 반올림
        return weight.divide(heightInMeter.pow(2), BMI_SCALE, RoundingMode.HALF_UP);
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

    // 도메인 검증 메서드
    private static BigDecimal requirePositive(BigDecimal value, String fieldName) {

        // 필수 값 검증
        if (value == null) {
            if ("height".equals(fieldName)) {
                throw new InbodyRequiredFieldException(InbodyErrorCode.HEIGHT_REQUIRED);
            }
            throw new InbodyRequiredFieldException(InbodyErrorCode.WEIGHT_REQUIRED);
        }
        // 최솟값 검증
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
