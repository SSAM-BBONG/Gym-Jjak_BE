package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReviewStatus;
import com.ssambbong.gymjjak.pt.trainerReview.domain.repository.TrainerReviewRepository;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerReviewReportTargetAdapterTest {

    @Mock
    private TrainerReviewRepository trainerReviewRepository;

    @InjectMocks
    private TrainerReviewReportTargetAdapter adapter;

    @Test
    @DisplayName("강사평 스냅샷 조회 시 title이 '강사평'이고 targetOwnerId가 userId로 매핑되어야 한다")
    void getSnapshot_success() {
        // Given
        TrainerReview trainerReview = TrainerReview.restore(
                1L, 77L, 5L, 10L, 5,
                "강사평 내용", TrainerReviewStatus.ACTIVE, null, null, null
        );
        when(trainerReviewRepository.findActiveById(1L)).thenReturn(Optional.of(trainerReview));

        // When
        ReportTargetSnapshot snapshot = adapter.getSnapshot(1L);

        // Then
        assertAll(
                () -> assertEquals(1L, snapshot.targetId()),
                () -> assertEquals(77L, snapshot.targetOwnerId()),
                () -> assertEquals("강사평", snapshot.title()),
                () -> assertEquals("강사평 내용", snapshot.content()),
                () -> assertNull(snapshot.fileUrl())
        );
    }

    @Test
    @DisplayName("존재하지 않는 강사평 ID로 스냅샷 조회 시 TrainerReviewNotFoundException이 발생해야 한다")
    void getSnapshot_trainerReviewNotFound_throwsException() {
        // Given
        when(trainerReviewRepository.findActiveById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TrainerReviewNotFoundException.class, () -> adapter.getSnapshot(999L));
    }
}
