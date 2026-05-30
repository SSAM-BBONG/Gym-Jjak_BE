package com.ssambbong.gymjjak.report.infrastructure.policy;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ssambbong.gymjjak.report.application.policy.ReportNumberGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class DefaultReportNumberGenerator implements ReportNumberGenerator {

    @Override
    public String generate() {
        // TSID 생성 (13자리 문자열 반환)
        return TsidCreator.getTsid().toString();
    }
}