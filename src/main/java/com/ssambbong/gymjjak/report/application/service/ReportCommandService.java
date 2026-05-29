package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.usecase.CreateReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportCommandService implements ReportCommandUseCase {
    @Override
    public Long createReport(CreateReportCommand createReportCommand) {
        return 0L;
    }
}
