package com.ssambbong.gymjjak.pt.feedback.presentation.api;

import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "피드백", description = "PT 피드백 관련 API")
@RestController
@RequestMapping("/api/reservations/{ptReservationId}/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackQueryUseCase feedbackQueryUseCase;
}
