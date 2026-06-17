package com.ssambbong.gymjjak.chat.application.usecase;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;

public interface ChatRoomUseCase {
    Long createChatRoom(CreateChatRoomCommand command);
    void leaveChatRoom(Long chatRoomId, Long requesterId);
    ChatRoomListResult getChatRooms(Long requesterId);
}
