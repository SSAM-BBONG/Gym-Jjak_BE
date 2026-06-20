package com.ssambbong.gymjjak.chat.presentation.api;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.presentation.api.mapper.ChatMapper;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatMessageListResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatMessageResponseCode;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat/rooms/{chatRoomId}/messages")
@RequiredArgsConstructor
@Validated
public class ChatMessageController {

    private final ChatMessageUseCase chatMessageUseCase;
    private final ChatMapper chatMapper;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "채팅 메시지 목록 조회", description = "채팅방의 메시지 목록을 커서 기반 페이지네이션으로 조회한다. cursor 미입력 시 최신 메시지부터 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatMessageListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음 (채팅방 참여자가 아님)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<ChatMessageListResponse>> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) @Min(1) Long cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        ChatMessageQuery query = new ChatMessageQuery(chatRoomId, cursor, size);
        ChatMessageListResult result = chatMessageUseCase.findMessages(authUser.userId(), query);
        return ResponseEntity.ok(GlobalApiResponse.ok(
                ChatMessageResponseCode.CHAT_MESSAGE_LIST_FETCHED,
                chatMapper.toListResponse(result)
        ));
    }
}
