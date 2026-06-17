package com.ssambbong.gymjjak.pt.feedback.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.FeedbackListResponse;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.FeedbackResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "피드백", description = "PT 피드백 관련 API")
@RestController
@RequestMapping("/api/reservations/{ptReservationId}/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackQueryUseCase feedbackQueryUseCase;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "피드백 목록 조회", description = "예약 ID로 커리큘럼별 피드백 목록을 조회한다.")
    public ResponseEntity<GlobalApiResponse<List<FeedbackListResponse>>>
    findfeedbacks(@AuthenticationPrincipal AuthUser authUser,
                  @PathVariable Long ptReservationId
    ) {
        List<FeedbackListResponse> responses = feedbackQueryUseCase
                .findFeedbacksByReservation(authUser.userId(), ptReservationId)
                .stream()
                .map(FeedbackListResponse::from)
                .toList();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        FeedbackResponseCode.RESERVATION_FEEDBACKS_FETCHED,
                        responses
                )
        );
    }
}
