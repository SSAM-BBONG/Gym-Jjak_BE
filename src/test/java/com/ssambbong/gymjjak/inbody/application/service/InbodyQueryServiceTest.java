package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.InbodyItemResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;
import com.ssambbong.gymjjak.inbody.domain.model.BmiStatus;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodySlice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InbodyQueryServiceTest {

    private static final Long USER_ID = 1L;

    private InbodyRepository inbodyRepository;
    private InbodyQueryService inbodyQueryService;

    @BeforeEach
    void setUp() {
        inbodyRepository = Mockito.mock(InbodyRepository.class);
        inbodyQueryService = new InbodyQueryService(inbodyRepository);
    }

    @Test
    void getInbodyList_success_latestInbodyIncludesChanges() {
        Inbody latestInbody = createInbody(
                2L, LocalDate.of(2026, 7, 14),
                "170.00", "70.00", "15.00", "30.00", "1600.00"
        );
        Inbody previousInbody = createInbody(
                1L, LocalDate.of(2026, 7, 1),
                "170.00", "68.00", "16.00", "29.00", "1500.00"
        );

        // 최신 2개와 더 보기 가능 여부 반환
        when(inbodyRepository.findInbodySlice(USER_ID, null, null, 2))
                .thenReturn(new InbodySlice(
                        List.of(latestInbody, previousInbody),
                        true
                ));

        InbodyListResult result = inbodyQueryService.getInbodyList(
                new GetInbodyListQuery(USER_ID, null, null)
        );

        InbodyItemResult latestResult = result.inbodies().get(0);
        InbodyItemResult previousResult = result.inbodies().get(1);

        assertThat(result.inbodies()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextMeasuredDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(result.nextInbodyId()).isEqualTo(1L);

        assertThat(latestResult.bmi()).isEqualByComparingTo("24.2");
        assertThat(latestResult.bmr()).isEqualByComparingTo("1600.00");
        assertThat(latestResult.bmiStatus()).isEqualTo(BmiStatus.OVERWEIGHT);
        assertThat(latestResult.weightChange()).isEqualByComparingTo("2.00");
        assertThat(latestResult.skeletalMuscleMassChange()).isEqualByComparingTo("1.00");
        assertThat(latestResult.bodyFatPercentageChange()).isEqualByComparingTo("-1.00");
        assertThat(latestResult.bmiChange()).isEqualByComparingTo("0.7");

        // 직전 기록은 최신 기록의 비교 기준이므로 변화량을 내려주지 않음
        assertThat(previousResult.weightChange()).isNull();
        assertThat(previousResult.skeletalMuscleMassChange()).isNull();
        assertThat(previousResult.bodyFatPercentageChange()).isNull();
        assertThat(previousResult.bmiChange()).isNull();
        assertThat(previousResult.bmr()).isEqualByComparingTo("1500.00");

        verify(inbodyRepository).findInbodySlice(USER_ID, null, null, 2);
    }

    @Test
    void getInbodyList_success_singleInbodyHasNoChanges() {
        Inbody latestInbody = createInbody(
                1L, LocalDate.of(2026, 7, 14),
                "170.00", "70.00", "15.00", "30.00", "1600.00"
        );

        when(inbodyRepository.findInbodySlice(USER_ID, null, null, 2))
                .thenReturn(new InbodySlice(List.of(latestInbody), false));

        InbodyListResult result = inbodyQueryService.getInbodyList(
                new GetInbodyListQuery(USER_ID, null, null)
        );

        assertThat(result.inbodies()).hasSize(1);

        InbodyItemResult latestResult = result.inbodies().get(0);

        assertThat(result.inbodies()).hasSize(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextMeasuredDate()).isNull();
        assertThat(result.nextInbodyId()).isNull();

        assertThat(latestResult.weightChange()).isNull();
        assertThat(latestResult.skeletalMuscleMassChange()).isNull();
        assertThat(latestResult.bodyFatPercentageChange()).isNull();
        assertThat(latestResult.bmiChange()).isNull();
    }

    private Inbody createInbody(
            Long id,
            LocalDate measuredDate,
            String height,
            String weight,
            String bodyFatPercentage,
            String skeletalMuscleMass,
            String bmr
    ) {
        return Inbody.reconstruct(
                id,
                USER_ID,
                measuredDate,
                new BigDecimal(height),
                new BigDecimal(weight),
                new BigDecimal(bodyFatPercentage),
                new BigDecimal(skeletalMuscleMass),
                new BigDecimal(bmr),
                null,
                null
        );
    }
}
