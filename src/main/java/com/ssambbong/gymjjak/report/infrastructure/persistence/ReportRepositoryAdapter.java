package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.exception.ReportNotFoundException;
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
    // 도메인 객체 <-> JPA 엔티티 변환 매퍼
    private final ReportPersistenceMapper reportPersistenceMapper;

    @Override
    public Optional<Report> findById(Long reportId) {
        return reportRepository.findById(reportId)
                .map(reportPersistenceMapper::toDomain);
    }

    @Override
    public Report save(Report report) {
        // 도멩니 -> 엔티티
        ReportJpaEntity entity = reportPersistenceMapper.toEntity(report);

        // 존재하면 기존 값에 업데이트
        if (report.getReportId() != null) {
            ReportJpaEntity existing = reportRepository.findById(report.getReportId())
                    .orElseThrow(() -> new ReportNotFoundException(report.getReportId()));

            // 변경 값 업데이트
            existing.updateFromDomain(report);

            return reportPersistenceMapper.toDomain(existing);
        }
        // 신규 저장
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

    @Override
    public boolean existsByReporterIdAndReportGroupId(Long reporterId, Long reportGroupId) {
        return reportRepository.existsByReporterIdAndReportGroupId(reporterId, reportGroupId);
    }
}
