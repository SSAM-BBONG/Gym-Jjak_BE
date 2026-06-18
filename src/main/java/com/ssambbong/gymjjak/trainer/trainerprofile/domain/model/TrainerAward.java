package com.ssambbong.gymjjak.trainer.trainerprofile.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrainerAward {

    private final Long trainerAwardId;
    private final Long trainerProfileId;
    private final String name;

    @Builder(access = AccessLevel.PUBLIC)
    private TrainerAward(
            Long trainerAwardId,
            Long trainerProfileId,
            String name
    ) {
        this.trainerAwardId = trainerAwardId;
        this.trainerProfileId = trainerProfileId;
        this.name = name;
    }

    public static TrainerAward create(Long trainerProfileId, String name) {
        return new TrainerAward(
                null,
                trainerProfileId,
                name
        );
    }

    public static TrainerAward restore(
            Long trainerAwardId,
            Long trainerProfileId,
            String name
    ) {
        return new TrainerAward(
                trainerAwardId,
                trainerProfileId,
                name
        );
    }
}