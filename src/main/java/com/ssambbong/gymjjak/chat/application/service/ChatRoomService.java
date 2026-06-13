package com.ssambbong.gymjjak.chat.application.service;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.exception.ChatRoomAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService implements ChatRoomUseCase {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public Long createChatRoom(CreateChatRoomCommand command) {
        if (chatRoomRepository.existsByUserIdAndTrainerProfileIdAndStatus(
                command.userId(), command.trainerProfileId(), ChatRoomStatus.ACTIVE)) {
            throw new ChatRoomAlreadyExistsException();
        }

        ChatRoom chatRoom = ChatRoom.create(command.userId(), command.trainerProfileId(), command.ptCourseId());
        ChatRoom saved = chatRoomRepository.save(chatRoom);
        log.info("채팅방 생성 완료 - chatRoomId: {}, userId: {}, trainerProfileId: {}",
                saved.getId(), command.userId(), command.trainerProfileId());
        return saved.getId();
    }
}
