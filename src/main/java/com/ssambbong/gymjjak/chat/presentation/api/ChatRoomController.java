package com.ssambbong.gymjjak.chat.presentation.api;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.presentation.api.request.CreateChatRoomRequest;
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
}
