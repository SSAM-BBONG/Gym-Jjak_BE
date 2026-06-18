package com.ssambbong.gymjjak.chat.application.service;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.port.TrainerView;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;

import java.util.List;
import com.ssambbong.gymjjak.chat.exception.ChatRoomAccessDeniedException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomAlreadyExistsException;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import com.ssambbong.gymjjak.chat.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.chat.exception.TrainerNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService implements ChatRoomUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final TrainerQueryPort trainerQueryPort;

    @Override
    @Transactional
    public Long createChatRoom(CreateChatRoomCommand command) {
        trainerQueryPort.findActiveTrainer(command.trainerProfileId())
                .orElseThrow(TrainerNotFoundException::new);

        if (chatRoomRepository.existsByUserIdAndTrainerProfileIdAndPtCourseIdAndStatus(
                command.userId(), command.trainerProfileId(), command.ptCourseId(), ChatRoomStatus.ACTIVE)) {
            throw new ChatRoomAlreadyExistsException();
        }

        ChatRoom chatRoom = ChatRoom.create(command.userId(), command.trainerProfileId(), command.ptCourseId());
        ChatRoom saved;
        try {
            saved = chatRoomRepository.save(chatRoom);
        } catch (DataIntegrityViolationException e) {
            if (isConstraintViolation(e, "uk_chat_rooms")) {
                throw new ChatRoomAlreadyExistsException();
            }
            if (isConstraintViolation(e, "fk_chat_rooms_trainer")) {
                throw new TrainerNotFoundException();
            }
            if (isConstraintViolation(e, "fk_chat_rooms_pt_course")) {
                throw new PtCourseNotFoundException();
            }
            throw e;
        }
        log.info("채팅방 생성 완료 - chatRoomId: {}, userId: {}, trainerProfileId: {}",
                saved.getId(), command.userId(), command.trainerProfileId());
        return saved.getId();
    }

    private boolean isConstraintViolation(DataIntegrityViolationException e, String constraintName) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConstraintViolationException cve) {
                String name = cve.getConstraintName();
                if (name != null && name.contains(constraintName)) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }

    @Override
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long requesterId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        if (requesterId.equals(chatRoom.getUserId())) {
            chatRoom.leaveAsUser();
        } else {
            Long trainerUserId = trainerQueryPort.findActiveTrainer(chatRoom.getTrainerProfileId())
                    .map(TrainerView::userId)
                    .orElseThrow(ChatRoomAccessDeniedException::new);
            if (requesterId.equals(trainerUserId)) {
                chatRoom.leaveAsTrainer();
            } else {
                throw new ChatRoomAccessDeniedException();
            }
        }

        chatRoomRepository.leaveChatRoom(chatRoom);
        log.info("채팅방 나가기 완료 - chatRoomId: {}, requesterId: {}, status: {}",
                chatRoomId, requesterId, chatRoom.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomListResult getChatRooms(Long requesterId) {
        List<ChatRoomSummary> rooms = chatRoomRepository.findChatRoomsByRequesterId(requesterId);
        long totalUnreadCount = rooms.stream().mapToLong(ChatRoomSummary::unreadCount).sum();
        return new ChatRoomListResult(rooms.size(), totalUnreadCount, rooms);
    }
}
