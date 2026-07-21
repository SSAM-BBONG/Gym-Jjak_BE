package com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;

public interface TrainerMainPageQueryUseCase {

    TrainerMainPageResult findMainPage(Long userId);
}
