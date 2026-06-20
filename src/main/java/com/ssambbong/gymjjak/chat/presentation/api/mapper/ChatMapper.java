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
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface ChatMapper {

    @Mapping(target = "partnerProfileImageUrl", source = "partnerProfileImageUrl")
    ChatRoomSummaryResponse toResponse(ChatRoomSummary summary);

    @Mapping(target = "chatRooms", source = "chatRooms")
    ChatRoomListResponse toListResponse(ChatRoomListResult result);

    ChatMessageResponse toResponse(ChatMessageItem item);

    @Mapping(target = "messages", source = "messages")
    ChatMessageListResponse toListResponse(ChatMessageListResult result);
}
