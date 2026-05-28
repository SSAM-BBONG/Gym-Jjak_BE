package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepository {
    @Override
    public Optional<Report> findById(Long reportGroupId) {
        return Optional.empty();
    }

    @Override
    public Report save(Report report) {
        return null;
    }
}
