package com.ssambbong.gymjjak.chat.presentation.api;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.presentation.api.request.CreateChatRoomRequest;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomListResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomResponseCode;
import com.ssambbong.gymjjak.chat.presentation.api.response.CreateChatRoomResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
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

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "채팅방 목록 조회", description = "내가 참여 중인 채팅방 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<ChatRoomListResponse>> getChatRooms(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        ChatRoomListResult result = chatRoomUseCase.getChatRooms(authUser.userId());
        return ResponseEntity.ok(GlobalApiResponse.ok(
                ChatRoomResponseCode.CHAT_ROOM_LIST_FETCHED,
                ChatRoomListResponse.from(result)
        ));
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "채팅방 생성", description = "회원이 트레이너와의 1:1 채팅방을 생성한다.")
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
    @PatchMapping("/{chatRoomId}/leave")
    public ResponseEntity<GlobalApiResponse<Void>> leaveChatRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long chatRoomId
    ) {
        chatRoomUseCase.leaveChatRoom(chatRoomId, authUser.userId());

        return ResponseEntity.ok(GlobalApiResponse.ok(ChatRoomResponseCode.CHAT_ROOM_LEFT));
    }
}
