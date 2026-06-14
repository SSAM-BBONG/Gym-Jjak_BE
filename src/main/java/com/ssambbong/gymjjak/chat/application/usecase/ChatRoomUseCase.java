package com.ssambbong.gymjjak.chat.application.usecase;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;

public interface ChatRoomUseCase {
    Long createChatRoom(CreateChatRoomCommand command);
}
