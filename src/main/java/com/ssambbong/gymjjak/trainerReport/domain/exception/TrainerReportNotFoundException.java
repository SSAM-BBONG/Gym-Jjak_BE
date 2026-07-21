package com.ssambbong.gymjjak.trainerReport.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerReportNotFoundException extends NotFoundException {

    public TrainerReportNotFoundException(Long trainerReportId) {
        super(TrainerReportErrorCode.NOT_FOUND);
        addContext("trainerReportId", trainerReportId);
    }
}
