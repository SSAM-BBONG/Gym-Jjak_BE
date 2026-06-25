package com.ssambbong.gymjjak.pt.feedback.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.feedback.application.command.DeleteFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackCommandUseCase;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.request.CreateFeedbackRequest;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.request.UpdateFeedbackRequest;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.CreateFeedbackResponse;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.FeedbackDetailResponse;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.FeedbackListResponse;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.FeedbackResponseCode;
import com.ssambbong.gymjjak.pt.feedback.presentation.api.response.UpdateFeedbackResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "피드백", description = "PT 피드백 관련 API")
@RestController
@RequestMapping("/api/reservations/{ptReservationId}/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackQueryUseCase feedbackQueryUseCase;
    private final FeedbackCommandUseCase feedbackCommandUseCase;

    // 피드백 목록 조회
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

    // 피드백 상세 조회
    @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "피드백 상세 조회", description = "피드백 ID로 상세 내용을 조회한다.")
    public ResponseEntity<GlobalApiResponse<FeedbackDetailResponse>>
    findFeedbackDetail(@AuthenticationPrincipal AuthUser authUser,
                       @PathVariable Long ptReservationId,
                       @PathVariable Long feedbackId
    ) {
        FeedbackDetailResponse response = FeedbackDetailResponse.from(
                feedbackQueryUseCase.findFeedbackDetail(authUser.userId(), ptReservationId, feedbackId)
        );
        return ResponseEntity.ok(
                GlobalApiResponse.ok(FeedbackResponseCode.FEEDBACK_DETAIL_FETCHED, response)
        );
    }

    // 피드백 등록
    @PostMapping
    @PreAuthorize("hasAnyAuthority('TRAINER')")
    @Operation(summary = "피드백 등록", description = "트레이너가 수강생의 PT 회차에 대한 피드백을 등록한다.")
    public ResponseEntity<GlobalApiResponse<CreateFeedbackResponse>>
    createFeedback(@AuthenticationPrincipal AuthUser authUser,
                   @PathVariable Long ptReservationId,
                   @RequestBody @Valid CreateFeedbackRequest request
    ) {
        Long feedbackId = feedbackCommandUseCase.createFeedback(
                request.toCommand(authUser.userId(), ptReservationId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GlobalApiResponse.created(FeedbackResponseCode.FEEDBACK_CREATED,
                        CreateFeedbackResponse.from(feedbackId))
        );
    }

    // 피드백 수정
    @PatchMapping("/{feedbackId}")
    @PreAuthorize("hasAnyAuthority('TRAINER')")
    @Operation(summary = "피드백 수정", description = "트레이너가 본인이 작성한 피드백을 수정한다.")
    public ResponseEntity<GlobalApiResponse<UpdateFeedbackResponse>>
    updateFeedback(@AuthenticationPrincipal AuthUser authUser,
                   @PathVariable Long ptReservationId,
                   @PathVariable Long feedbackId,
                   @RequestBody @Valid UpdateFeedbackRequest request
    ) {
        Long updatedId = feedbackCommandUseCase.updateFeedback(
                request.toCommand(authUser.userId(), ptReservationId, feedbackId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(FeedbackResponseCode.FEEDBACK_UPDATED,
                        UpdateFeedbackResponse.from(updatedId))
        );
    }

    // 피드백 삭제
    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasAnyAuthority('TRAINER')")
    @Operation(summary = "피드백 삭제", description = "트레이너가 본인이 작성한 피드백을 삭제한다. 예약이 COMPLETED 상태이면 삭제 불가.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteFeedback(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptReservationId,
            @PathVariable Long feedbackId
    ) {
        feedbackCommandUseCase.deleteFeedback(
                new DeleteFeedbackCommand(authUser.userId(), ptReservationId, feedbackId));
        return ResponseEntity.ok(GlobalApiResponse.ok(FeedbackResponseCode.FEEDBACK_DELETED, null));
    }

}
