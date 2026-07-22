package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.command.DeleteInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.command.UpdateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyNotFoundException;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyUpdateNotAllowedException;
import com.ssambbong.gymjjak.inbody.domain.exception.InvalidInbodyValueException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InbodyCommandServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long INBODY_ID = 10L;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Clock TEST_CLOCK = Clock.fixed(
            Instant.parse("2026-07-15T01:00:00Z"),
            KOREA_ZONE_ID
    );

    private InbodyRepository inbodyRepository;
    private InbodyCommandService inbodyCommandService;

    @BeforeEach
    void setUp() {
        inbodyRepository = mock(InbodyRepository.class);
        inbodyCommandService = new InbodyCommandService(inbodyRepository, TEST_CLOCK);
    }

    @Test
    void createInbody_success_savesBmr() {
        CreateInbodyCommand command = new CreateInbodyCommand(
                USER_ID,
                LocalDate.of(2026, 7, 15),
                new BigDecimal("170.00"),
                new BigDecimal("68.00"),
                new BigDecimal("15.00"),
                new BigDecimal("30.00"),
                new BigDecimal("1500.00")
        );
        Inbody savedInbody = createInbody(LocalDateTime.of(2026, 7, 15, 9, 0));

        when(inbodyRepository.existsByUserIdAndMeasuredDate(USER_ID, command.measuredDate()))
                .thenReturn(false);
        when(inbodyRepository.save(any(Inbody.class))).thenReturn(savedInbody);

        CreateInbodyResult result = inbodyCommandService.createInbody(command);

        ArgumentCaptor<Inbody> inbodyCaptor = ArgumentCaptor.forClass(Inbody.class);
        verify(inbodyRepository).save(inbodyCaptor.capture());
        assertThat(inbodyCaptor.getValue().getBmr()).isEqualByComparingTo("1500.00");
        assertThat(result.inbodyId()).isEqualTo(INBODY_ID);
    }

    @Test
    void updateInbody_success_updatesTodayCreatedInbody() {
        Inbody inbody = createInbody(LocalDateTime.of(2026, 7, 15, 9, 0));
        UpdateInbodyCommand command = updateCommand(
                "175.00", "70.00", "14.50", "31.00", "1600.00"
        );

        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.of(inbody));
        when(inbodyRepository.save(inbody)).thenReturn(inbody);

        inbodyCommandService.updateInbody(command, INBODY_ID);

        assertThat(inbody.getHeight()).isEqualByComparingTo("175.00");
        assertThat(inbody.getWeight()).isEqualByComparingTo("70.00");
        assertThat(inbody.getBodyFatPercentage()).isEqualByComparingTo("14.50");
        assertThat(inbody.getSkeletalMuscleMass()).isEqualByComparingTo("31.00");
        assertThat(inbody.getBmr()).isEqualByComparingTo("1600.00");
        verify(inbodyRepository).save(inbody);
    }

    @Test
    void updateInbody_throwsException_whenInbodyDoesNotBelongToUser() {
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inbodyCommandService.updateInbody(
                updateCommand("175.00", "70.00", "14.50", "31.00", "1600.00"),
                INBODY_ID
        )).isInstanceOf(InbodyNotFoundException.class);

        verify(inbodyRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateInbody_throwsException_whenInbodyWasCreatedBeforeToday() {
        Inbody inbody = createInbody(LocalDateTime.of(2026, 7, 14, 23, 59));
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.of(inbody));

        assertThatThrownBy(() -> inbodyCommandService.updateInbody(
                updateCommand("175.00", "70.00", "14.50", "31.00", "1600.00"),
                INBODY_ID
        )).isInstanceOf(InbodyUpdateNotAllowedException.class);

        verify(inbodyRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateInbody_throwsException_whenHeightIsInvalid() {
        Inbody inbody = createInbody(LocalDateTime.of(2026, 7, 15, 9, 0));
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.of(inbody));

        assertThatThrownBy(() -> inbodyCommandService.updateInbody(
                updateCommand("0.00", "70.00", "14.50", "31.00", "1600.00"),
                INBODY_ID
        )).isInstanceOf(InvalidInbodyValueException.class);

        assertThat(inbody.getHeight()).isEqualByComparingTo("170.00");
        verify(inbodyRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateInbody_throwsException_whenBmrIsInvalid() {
        Inbody inbody = createInbody(LocalDateTime.of(2026, 7, 15, 9, 0));
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.of(inbody));

        assertThatThrownBy(() -> inbodyCommandService.updateInbody(
                updateCommand("175.00", "70.00", "14.50", "31.00", "-1.00"),
                INBODY_ID
        )).isInstanceOf(InvalidInbodyValueException.class);

        assertThat(inbody.getBmr()).isEqualByComparingTo("1500.00");
        verify(inbodyRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deleteInbody_success_deletesOwnedInbody() {
        Inbody inbody = createInbody(LocalDateTime.of(2026, 7, 15, 9, 0));
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.of(inbody));

        inbodyCommandService.deleteInbody(new DeleteInbodyCommand(USER_ID, INBODY_ID));

        verify(inbodyRepository).deleteById(INBODY_ID);
    }

    @Test
    void deleteInbody_throwsException_whenInbodyDoesNotBelongToUser() {
        when(inbodyRepository.findByIdAndUserId(INBODY_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inbodyCommandService.deleteInbody(
                new DeleteInbodyCommand(USER_ID, INBODY_ID)
        )).isInstanceOf(InbodyNotFoundException.class);

        verify(inbodyRepository, never()).deleteById(INBODY_ID);
    }

    private UpdateInbodyCommand updateCommand(
            String height,
            String weight,
            String bodyFatPercentage,
            String skeletalMuscleMass,
            String bmr
    ) {
        return new UpdateInbodyCommand(
                USER_ID,
                new BigDecimal(height),
                new BigDecimal(weight),
                new BigDecimal(bodyFatPercentage),
                new BigDecimal(skeletalMuscleMass),
                new BigDecimal(bmr)
        );
    }

    private Inbody createInbody(LocalDateTime createdAt) {
        return Inbody.reconstruct(
                INBODY_ID,
                USER_ID,
                LocalDate.of(2026, 7, 15),
                new BigDecimal("170.00"),
                new BigDecimal("68.00"),
                new BigDecimal("15.00"),
                new BigDecimal("30.00"),
                new BigDecimal("1500.00"),
                createdAt,
                createdAt
        );
    }
}
