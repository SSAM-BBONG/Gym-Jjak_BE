package com.ssambbong.gymjjak.trainerReport.application.service;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportDetailResult;
import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportListItem;
import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportListResult;
import com.ssambbong.gymjjak.trainerReport.application.usecase.TrainerReportQueryUseCase;
import com.ssambbong.gymjjak.trainerReport.domain.exception.TrainerReportNotFoundException;
import com.ssambbong.gymjjak.trainerReport.domain.model.TrainerReport;
import com.ssambbong.gymjjak.trainerReport.domain.repository.TrainerReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerReportQueryService implements TrainerReportQueryUseCase {

    private final TrainerReportRepository trainerReportRepository;
    private final SpringDataTrainerProfileRepository trainerProfileRepository;

    @Override
    public TrainerReportListResult findMyReports(Long userId, int page, int size) {
        Long trainerProfileId = resolveTrainerProfileId(userId);

        // hasNext 판단을 위해 요청 size보다 1건 더 조회한다.
        List<TrainerReport> rows = trainerReportRepository.findAllByTrainerProfileId(trainerProfileId, page, size + 1);

        boolean hasNext = rows.size() > size;
        List<TrainerReportListItem> items = rows.stream()
                .limit(size)
                .map(r -> new TrainerReportListItem(r.getId(), r.getTargetMonth()))
                .toList();

        return new TrainerReportListResult(items, hasNext);
    }

    @Override
    public TrainerReportDetailResult findMyReportDetail(Long userId, Long trainerReportId) {
        Long trainerProfileId = resolveTrainerProfileId(userId);

        TrainerReport report = trainerReportRepository.findByIdAndTrainerProfileId(trainerReportId, trainerProfileId)
                .orElseThrow(() -> new TrainerReportNotFoundException(trainerReportId));

        return new TrainerReportDetailResult(
                report.getId(), report.getTargetMonth(), report.getReport(), report.getMarketTrendsSnapshot());
    }

    private Long resolveTrainerProfileId(Long userId) {
        return trainerProfileRepository
                .findTrainerProfileIdByUserIdAndStatus(userId, TrainerProfileStatus.ACTIVE)
                .orElseThrow(() -> new TrainerProfileNotFoundException("userId", userId));
    }
}
