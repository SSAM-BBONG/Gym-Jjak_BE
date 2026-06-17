package com.ssambbong.gymjjak.chat.application.service;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements ChatMessageUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public ChatMessageListResult getMessages(Long requesterId, ChatMessageQuery query) {
        chatRoomRepository.findByIdAndParticipant(query.chatRoomId(), requesterId)
                .orElseThrow(ChatRoomNotFoundException::new);

        return chatMessageRepository.findMessages(query);
    }
}
