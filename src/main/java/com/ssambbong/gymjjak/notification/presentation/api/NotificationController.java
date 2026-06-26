package com.ssambbong.gymjjak.notification.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.notification.application.command.MarkNotificationReadCommand;
import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.MarkNotificationReadResult;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationCommandUseCase;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationQueryUseCase;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationUserCommandUseCase;
import com.ssambbong.gymjjak.notification.presentation.api.request.FindNotificationsRequest;
import com.ssambbong.gymjjak.notification.presentation.api.request.MarkNotificationReadRequest;
import com.ssambbong.gymjjak.notification.presentation.api.response.MarkNotificationReadResponse;
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
    private final NotificationUserCommandUseCase userCommandUseCase;

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

    @Operation(
            summary = "알림 읽음 처리",
            description = """
                로그인한 사용자의 알림을 읽음 처리합니다.

                notificationIds에 알림 ID를 1개만 전달하면 단일 읽음 처리,
                여러 개를 전달하면 선택 알림 읽음 처리가 가능합니다.

                이미 읽은 알림을 다시 요청해도 성공 처리됩니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "다른 사용자의 알림에 접근"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/read")
    public ResponseEntity<GlobalApiResponse<MarkNotificationReadResponse>> readNotifications(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid MarkNotificationReadRequest request
            ) {

        MarkNotificationReadResult result =
                userCommandUseCase.readNotifications(
                        new MarkNotificationReadCommand(
                                authUser.userId(),
                                request.notificationIds()
                        )
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        NotificationResponseCode.NOTIFICATION_READ,
                        MarkNotificationReadResponse.from(result)
                )
        );
    }
}
