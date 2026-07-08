package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackSanctionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackSanctionAdapter implements FeedbackSanctionPort {

    private final FeedbackRepository feedbackRepository;

    @Override
    @Transactional
    public void applySanction(Long targetId, ReportSanctionAction action) {
        log.debug("[FeedbackSanction] feedbackId={}, action={}", targetId, action);

        switch (action) {
            case APPLY_MANUAL_BLIND -> feedbackRepository.deleteById(targetId);
        }

        log.info("[FeedbackSanction] feedbackId={}, action={}", targetId, action);
    }
}
