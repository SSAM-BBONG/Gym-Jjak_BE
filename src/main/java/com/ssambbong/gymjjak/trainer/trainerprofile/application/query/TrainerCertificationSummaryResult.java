package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;

public record TrainerCertificationSummaryResult(
        Long trainerCertificationId,
        String name,
        TrainerCertificationType certificationType
) {
}
