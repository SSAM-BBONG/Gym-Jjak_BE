package com.ssambbong.gymjjak.notification.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationQueryUseCase;
import com.ssambbong.gymjjak.notification.presentation.api.request.FindNotificationsRequest;
import com.ssambbong.gymjjak.notification.presentation.api.response.NotificationListResponse;
import com.ssambbong.gymjjak.notification.presentation.api.response.NotificationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryUseCase queryUseCase;

    @Operation(
            summary = "내 알림 목록 조회",
            description = """
                    로그인한 사용자의 알림 목록을 최신순으로 조회합니다.
                    
                    삭제된 알림은 조회되지 않습니다.
                    만료된 알림은 조회되지 않습니다.
                    기본 size는 10개이며, page는 0부터 시작합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<NotificationListResponse>> findNotifications(
            @AuthenticationPrincipal AuthUser authUser,
            @ModelAttribute @Valid FindNotificationsRequest request
    ) {
        NotificationListResult result =
                queryUseCase.findNotifications(
                        new FindNotificationsQuery(
                                authUser.userId(),
                                request.resolvePage(),
                                request.resolveSize()
                        )
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        NotificationResponseCode.NOTIFICATION_LIST_FOUND,
                        NotificationListResponse.from(result)
                )
        );
    }
}
