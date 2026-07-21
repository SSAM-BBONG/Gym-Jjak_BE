package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MealAnalysisAdapterTest {

    @Mock
    private SpringDataMealAnalysisRepository repository;

    @Mock
    private MealAnalysisPersistenceMapper persistenceMapper;

    @InjectMocks
    private MealAnalysisAdapter adapter;

    @Test
    void 날짜가_없으면_전체_기간의_식단을_조회한다() {
        given(repository.findAllByUserId(any(), any(Pageable.class))).willReturn(Page.empty());

        adapter.findAllByUserId(new MealPageQuery(10L, 0, 20, null));

        verify(repository).findAllByUserId(eq(10L), any(Pageable.class));
    }

    @Test
    void 날짜가_있으면_해당_날짜의_식단만_조회한다() {
        LocalDate date = LocalDate.of(2026, 7, 21);
        LocalDateTime startInclusive = date.atStartOfDay();
        LocalDateTime endExclusive = date.plusDays(1).atStartOfDay();
        given(repository.findAllByUserIdAndMealTimeGreaterThanEqualAndMealTimeLessThan(
                any(), any(), any(), any(Pageable.class))).willReturn(Page.empty());

        adapter.findAllByUserId(new MealPageQuery(10L, 0, 20, date));

        verify(repository).findAllByUserIdAndMealTimeGreaterThanEqualAndMealTimeLessThan(
                eq(10L), eq(startInclusive), eq(endExclusive), any(Pageable.class));
    }
}
