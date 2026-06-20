package com.ssambbong.gymjjak.chat.application.service;

import com.ssambbong.gymjjak.chat.application.command.SendChatMessageCommand;
import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.exception.ChatRoomAccessDeniedException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomClosedException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import com.ssambbong.gymjjak.chat.exception.TrainerNotFoundException;
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
    public void markAsRead(Long messageId) {
        chatMessageRepository.markAsRead(messageId);
    }

    @Override
    @Transactional
    public ChatMessage createMessage(SendChatMessageCommand command) {
        validateParticipant(command.chatRoomId(), command.senderId());

        ChatMessage message = ChatMessage.create(command.chatRoomId(), command.senderId(), command.content());
        return chatMessageRepository.save(message);
    }

    @Override
    @Transactional
    public ChatMessageListResult findMessages(Long requesterId, ChatMessageQuery query) {
        validateParticipant(query.chatRoomId(), requesterId);

        return chatMessageRepository.findMessages(query, requesterId);
    }

    private void validateParticipant(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        if (chatRoom.getStatus() != ChatRoomStatus.ACTIVE) {
            throw new ChatRoomClosedException();
        }

        if (!userId.equals(chatRoom.getUserId())) {
            Long trainerUserId = trainerQueryPort.findActiveTrainerUserId(chatRoom.getTrainerProfileId())
                    .orElseThrow(TrainerNotFoundException::new);
            if (!userId.equals(trainerUserId)) {
                throw new ChatRoomAccessDeniedException();
            }
        }
    }
}
