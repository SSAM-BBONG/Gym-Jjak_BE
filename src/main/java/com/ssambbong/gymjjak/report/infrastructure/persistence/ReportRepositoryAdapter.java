package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepository {

    private final SpringDataReportRepository reportRepository;
    private final ReportPersistenceMapper reportPersistenceMapper;

    @Override
    public Optional<Report> findById(Long reportId) {
        return reportRepository.findById(reportId)
                .map(reportPersistenceMapper::toDomain);
    }

    @Override
    public Report save(Report report) {
        ReportJpaEntity entity = reportPersistenceMapper.toEntity(report);
        ReportJpaEntity savedEntity = reportRepository.save(entity);
        return reportPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<Report> findAllByReportGroupId(Long reportGroupId) {
        return reportRepository.findByReportGroupIdOrderByCreatedAtDesc(reportGroupId)
                .stream()
                .map(reportPersistenceMapper::toDomain)
                .toList();
    }
}
