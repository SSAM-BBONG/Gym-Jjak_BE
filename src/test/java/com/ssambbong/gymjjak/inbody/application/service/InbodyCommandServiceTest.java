package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.domain.exception.DuplicateInbodyMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.exception.FutureMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InbodyCommandServiceTest {

    private InbodyRepository inbodyRepository;
    private InbodyCommandService inbodyCommandService;
    private static final Clock TEST_CLOCK = Clock.fixed(
            Instant.parse("2026-07-13T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );

    @BeforeEach
    void setUp() {
        inbodyRepository = mock(InbodyRepository.class);
        inbodyCommandService = new InbodyCommandService(
                inbodyRepository,
                TEST_CLOCK  );
    }

    @Test
    @DisplayName("인바디 기록을 생성하면 생성된 인바디 ID를 반환한다")
    void createInbody_success() {
        CreateInbodyCommand command = createCommand(LocalDate.of(2026, 7, 13));
        Inbody savedInbody = Inbody.reconstruct(
                1L,
                command.userId(),
                command.measuredDate(),
                command.height(),
                command.weight(),
                command.bodyFatPercentage(),
                command.skeletalMuscleMass(),
                LocalDateTime.of(2026, 7, 13, 10, 0),
                LocalDateTime.of(2026, 7, 13, 10, 0)
        );
        when(inbodyRepository.existsByUserIdAndMeasuredDate(command.userId(), command.measuredDate()))
                .thenReturn(false);
        when(inbodyRepository.save(any(Inbody.class))).thenReturn(savedInbody);

        CreateInbodyResult result = inbodyCommandService.createInbody(command);

        assertThat(result.inbodyId()).isEqualTo(1L);
        verify(inbodyRepository).save(any(Inbody.class));
    }

    @Test
    @DisplayName("동일한 측정일의 인바디 기록이 있으면 생성에 실패한다")
    void createInbody_fail_duplicateMeasuredDate() {
        CreateInbodyCommand command = createCommand(LocalDate.of(2026, 7, 13));
        when(inbodyRepository.existsByUserIdAndMeasuredDate(command.userId(), command.measuredDate()))
                .thenReturn(true);

        assertThatThrownBy(() -> inbodyCommandService.createInbody(command))
                .isInstanceOf(DuplicateInbodyMeasuredDateException.class);

        verify(inbodyRepository, never()).save(any(Inbody.class));
    }

    @Test
    @DisplayName("미래 측정일의 인바디 기록은 생성할 수 없다")
    void createInbody_fail_futureMeasuredDate() {
        CreateInbodyCommand command = createCommand(
                LocalDate.now(TEST_CLOCK).plusDays(1)
        );

        assertThatThrownBy(() -> inbodyCommandService.createInbody(command))
                .isInstanceOf(FutureMeasuredDateException.class);

        verify(inbodyRepository, never()).existsByUserIdAndMeasuredDate(any(), any());
        verify(inbodyRepository, never()).save(any(Inbody.class));
    }

    private CreateInbodyCommand createCommand(LocalDate measuredDate) {
        return new CreateInbodyCommand(
                1L,
                measuredDate,
                new BigDecimal("170.00"),
                new BigDecimal("70.00"),
                new BigDecimal("15.50"),
                new BigDecimal("30.20")
        );
    }
}
