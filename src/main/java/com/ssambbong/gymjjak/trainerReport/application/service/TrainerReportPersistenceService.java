package com.ssambbong.gymjjak.trainerReport.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.trainerReport.application.event.TrainerReportGeneratedEvent;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.MarketTrendsSnapshot;
import com.ssambbong.gymjjak.trainerReport.domain.model.TrainerReport;
import com.ssambbong.gymjjak.trainerReport.domain.repository.TrainerReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrainerReportPersistenceService {

    private final TrainerReportRepository trainerReportRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long save(Long trainerProfileId, Long receiverUserId, LocalDate targetMonth, String report,
                      MarketTrendsSnapshot marketTrends) {
        String snapshotJson = serialize(marketTrends);

        Long trainerReportId = trainerReportRepository.save(
                TrainerReport.create(trainerProfileId, targetMonth, report, snapshotJson));

        // AFTER_COMMIT 리스너가 이 이벤트를 받아 알림을 생성한다 — 저장 트랜잭션 안에서 발행해야
        // 커밋 시점에 정확히 걸린다(조율 서비스에는 트랜잭션이 없어서 여기서 발행함).
        eventPublisher.publishEvent(new TrainerReportGeneratedEvent(receiverUserId, trainerReportId));

        return trainerReportId;
    }

    private String serialize(MarketTrendsSnapshot marketTrends) {
        try {
            return objectMapper.writeValueAsString(marketTrends);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("시장 데이터 스냅샷 직렬화에 실패했습니다.", exception);
        }
    }
}
