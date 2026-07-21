package com.ssambbong.gymjjak.chat.presentation.api;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.presentation.api.mapper.ChatMapper;
import com.ssambbong.gymjjak.chat.presentation.api.request.CreateChatRoomRequest;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomListResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomResponseCode;
import com.ssambbong.gymjjak.chat.presentation.api.response.CreateChatRoomResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.UnreadCountResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatMapper chatMapper;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "채팅방 목록 조회", description = "내가 참여 중인 채팅방 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatRoomListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<ChatRoomListResponse>> getChatRooms(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        ChatRoomListResult result = chatRoomUseCase.getChatRooms(authUser.userId());
        return ResponseEntity.ok(GlobalApiResponse.ok(
                ChatRoomResponseCode.CHAT_ROOM_LIST_FETCHED,
                chatMapper.toListResponse(result)
        ));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "전체 안 읽은 메시지 수 조회", description = "내 모든 채팅방의 안 읽은 메시지 총 개수를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UnreadCountResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/unread-count")
    public ResponseEntity<GlobalApiResponse<UnreadCountResponse>> getTotalUnreadCount(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        long count = chatRoomUseCase.getTotalUnreadCount(authUser.userId());
        return ResponseEntity.ok(GlobalApiResponse.ok(
                ChatRoomResponseCode.CHAT_UNREAD_COUNT_FETCHED,
                new UnreadCountResponse(count)
        ));
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "채팅방 생성", description = "회원이 트레이너와의 1:1 채팅방을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateChatRoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패 또는 유효하지 않은 PT 코스)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음 (USER만 채팅방 생성 가능)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 채팅방",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreateChatRoomResponse>> createChatRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateChatRoomRequest request
    ) {
        CreateChatRoomCommand command = new CreateChatRoomCommand(
                authUser.userId(),
                request.trainerProfileId(),
                request.ptCourseId()
        );

        Long chatRoomId = chatRoomUseCase.createChatRoom(command);

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(ChatRoomResponseCode.CHAT_ROOM_CREATED,
                        new CreateChatRoomResponse(chatRoomId)));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "채팅방 나가기", description = "회원 또는 트레이너가 채팅방을 나간다. 한 명이 나가면 CLOSED, 둘 다 나가면 DELETED 상태로 전환된다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 나가기 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "이미 종료된 채팅방 (DELETED 상태)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음 (채팅방 참여자가 아님)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "이미 나간 채팅방",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/{chatRoomId}/leave")
    public ResponseEntity<GlobalApiResponse<Void>> leaveChatRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long chatRoomId
    ) {
        chatRoomUseCase.leaveChatRoom(chatRoomId, authUser.userId());

        return ResponseEntity.ok(GlobalApiResponse.ok(ChatRoomResponseCode.CHAT_ROOM_LEFT));
    }
}
