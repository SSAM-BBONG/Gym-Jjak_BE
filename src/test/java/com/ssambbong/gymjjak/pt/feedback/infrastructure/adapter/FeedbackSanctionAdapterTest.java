package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackSanctionAdapterTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackSanctionAdapter adapter;

    @Test
    @DisplayName("수동 블라인드 적용 시 피드백이 소프트딜리트되어야 한다")
    void applySanction_manualBlind_callsDeleteById() {
        // When
        adapter.applySanction(1L, ReportSanctionAction.APPLY_MANUAL_BLIND);

        // Then
        verify(feedbackRepository).deleteById(1L);
    }
}
