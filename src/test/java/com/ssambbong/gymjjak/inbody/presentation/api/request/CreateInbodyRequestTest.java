package com.ssambbong.gymjjak.inbody.presentation.api.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInbodyRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("소수점 셋째 자리의 키는 요청 검증에 실패한다")
    void validate_fail_heightFractionOverTwoDigits() {
        CreateInbodyRequest request = new CreateInbodyRequest(
                LocalDate.of(2026, 7, 13),
                new BigDecimal("170.123"),
                new BigDecimal("70.00"),
                new BigDecimal("15.50"),
                new BigDecimal("30.20")
        );

        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("height"));
    }
}
