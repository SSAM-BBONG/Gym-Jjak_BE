package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;

import java.math.BigDecimal;
import java.util.List;

public record MyTrainerProfileResult(
    Long trainerProfileId,
    String profileImageUrl,
    String profileImageOriginalName,
    String trainerName,
    String introduction,
    BigDecimal averageRating,
    int reviewCount,
    TrainerProfileStatus status,
    List<TrainerCertificationResult> certifications,
    List<TrainerAwardResult> awards
) {

    public MyTrainerProfileResult{
        certifications = certifications == null
                ? List.of()
                : List.copyOf(certifications);

        awards = awards == null
                ? List.of()
                : List.copyOf(awards);
    }
}
