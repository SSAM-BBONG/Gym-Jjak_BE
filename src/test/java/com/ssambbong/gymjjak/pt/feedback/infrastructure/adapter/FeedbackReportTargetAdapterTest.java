package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackStatus;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackReportTargetAdapterTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private TrainerProfileQueryPort trainerProfileQueryPort;

    @InjectMocks
    private FeedbackReportTargetAdapter adapter;

    @Test
    @DisplayName("피드백 스냅샷 조회 시 title이 '피드백'이고 targetOwnerId가 올바르게 매핑되어야 한다")
    void getSnapshot_success() {
        // Given
        Feedback feedback = Feedback.restore(
                1L, 10L, 2L, 5L, 99L,
                "피드백 내용", FeedbackStatus.ACTIVE, LocalDateTime.now()
        );
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(trainerProfileQueryPort.findUserIdByTrainerProfileId(5L)).thenReturn(99L);

        // When
        ReportTargetSnapshot snapshot = adapter.getSnapshot(1L);

        // Then
        assertAll(
                () -> assertEquals(1L, snapshot.targetId()),
                () -> assertEquals(99L, snapshot.targetOwnerId()),
                () -> assertEquals("피드백", snapshot.title()),
                () -> assertEquals("피드백 내용", snapshot.content()),
                () -> assertNull(snapshot.fileUrl())
        );
    }

    @Test
    @DisplayName("존재하지 않는 피드백 ID로 스냅샷 조회 시 FeedbackNotFoundException이 발생해야 한다")
    void getSnapshot_feedbackNotFound_throwsException() {
        // Given
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(FeedbackNotFoundException.class, () -> adapter.getSnapshot(999L));

        verify(trainerProfileQueryPort, never()).findUserIdByTrainerProfileId(any());
    }
}
