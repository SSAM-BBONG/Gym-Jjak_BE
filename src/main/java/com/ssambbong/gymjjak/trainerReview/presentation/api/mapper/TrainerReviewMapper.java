package com.ssambbong.gymjjak.trainerReview.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.presentation.api.request.CreateTrainerReviewRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface TrainerReviewMapper {

    CreateTrainerReviewCommand toCommand(CreateTrainerReviewRequest request, Long userId, Long ptCourseId);
}
