package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReviewStatus;
import com.ssambbong.gymjjak.pt.trainerReview.domain.repository.TrainerReviewRepository;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerReviewSanctionAdapterTest {

    @Mock
    private TrainerReviewRepository trainerReviewRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private TrainerReviewSanctionAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-17T00:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
    }

    @Test
    @DisplayName("수동 블라인드 적용 시 강사평 status가 DELETED로 소프트딜리트되어야 한다")
    void applySanction_manualBlind_softDeletesTrainerReview() {
        // Given
        TrainerReview trainerReview = TrainerReview.restore(
                1L, 77L, 5L, 10L, 5,
                "강사평 내용", TrainerReviewStatus.ACTIVE, null, null, null
        );
        when(trainerReviewRepository.findActiveById(1L)).thenReturn(Optional.of(trainerReview));

        // When
        adapter.applySanction(1L, ReportSanctionAction.APPLY_MANUAL_BLIND);

        // Then
        verify(trainerReviewRepository).save(argThat(saved ->
                saved.getStatus() == TrainerReviewStatus.DELETED
        ));
    }

    @Test
    @DisplayName("존재하지 않는 강사평 ID로 제재 시 TrainerReviewNotFoundException이 발생해야 한다")
    void applySanction_trainerReviewNotFound_throwsException() {
        // Given
        when(trainerReviewRepository.findActiveById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TrainerReviewNotFoundException.class,
                () -> adapter.applySanction(999L, ReportSanctionAction.APPLY_MANUAL_BLIND));

        verify(trainerReviewRepository, never()).save(any());
    }
}
