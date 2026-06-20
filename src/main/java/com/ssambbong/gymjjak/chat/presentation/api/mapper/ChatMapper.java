package com.ssambbong.gymjjak.chat.presentation.api.mapper;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageItem;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatMessageListResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatMessageResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomListResponse;
import com.ssambbong.gymjjak.chat.presentation.api.response.ChatRoomSummaryResponse;
import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ChatMapper {

    ChatRoomSummaryResponse toResponse(ChatRoomSummary summary);

    ChatRoomListResponse toListResponse(ChatRoomListResult result);

    ChatMessageResponse toResponse(ChatMessageItem item);

    ChatMessageListResponse toListResponse(ChatMessageListResult result);
}
