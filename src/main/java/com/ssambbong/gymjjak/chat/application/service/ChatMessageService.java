package com.ssambbong.gymjjak.chat.application.service;

import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.port.TrainerView;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.exception.ChatRoomAccessDeniedException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements ChatMessageUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TrainerQueryPort trainerQueryPort;

    @Override
    @Transactional
    public ChatMessageListResult getMessages(Long requesterId, ChatMessageQuery query) {
        ChatRoom chatRoom = chatRoomRepository.findById(query.chatRoomId())
                .orElseThrow(ChatRoomNotFoundException::new);

        if (!requesterId.equals(chatRoom.getUserId())) {
            Long trainerUserId = trainerQueryPort.findActiveTrainer(chatRoom.getTrainerProfileId())
                    .map(TrainerView::userId)
                    .orElseThrow(ChatRoomAccessDeniedException::new);
            if (!requesterId.equals(trainerUserId)) {
                throw new ChatRoomAccessDeniedException();
            }
        }

        return chatMessageRepository.findMessages(query, requesterId);
    }
}
