package com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerCondition;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;

public interface TrainerProfileSearchQueryPort {

    SearchTrainerListResult searchTrainers(
            SearchTrainerCondition condition
    );
}
