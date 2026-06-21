package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;

public record TrainerCertificationResult(
        Long trainerCertificationId,
        String name,
        TrainerCertificationType certificationType,
        String fileUrl
) {
}
