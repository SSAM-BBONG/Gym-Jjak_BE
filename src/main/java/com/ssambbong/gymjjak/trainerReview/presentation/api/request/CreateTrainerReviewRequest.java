package com.ssambbong.gymjjak.trainerReview.presentation.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTrainerReviewRequest(
        @NotNull @Positive Long ptReservationId,
        @Min(1) @Max(5) int rating,
        @NotBlank @Size(max = 500) String content
) {
}
