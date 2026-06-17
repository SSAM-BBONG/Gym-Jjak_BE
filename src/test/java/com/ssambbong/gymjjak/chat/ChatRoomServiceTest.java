package com.ssambbong.gymjjak.chat;

import com.ssambbong.gymjjak.chat.application.command.CreateChatRoomCommand;
import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.port.TrainerView;
import com.ssambbong.gymjjak.chat.application.service.ChatRoomService;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.exception.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private TrainerQueryPort trainerQueryPort;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChatRoom {

        private final CreateChatRoomCommand command = new CreateChatRoomCommand(1L, 11L, 1L);

        @Test
        @DisplayName("정상적으로 채팅방을 생성한다")
        void success() {
            ChatRoom savedRoom = ChatRoom.restore(5L, 1L, 11L, 1L, false, false,
                    ChatRoomStatus.ACTIVE, LocalDateTime.now(), null, null, LocalDateTime.now());

            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.of(new TrainerView(11L)));
            when(chatRoomRepository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(1L, 11L, 1L, ChatRoomStatus.ACTIVE)).thenReturn(false);
            when(chatRoomRepository.save(any())).thenReturn(savedRoom);

            Long result = chatRoomService.createChatRoom(command);

            assertThat(result).isEqualTo(5L);
            verify(chatRoomRepository).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 트레이너이면 TrainerNotFoundException이 발생한다")
        void fail_trainerNotFound() {
            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatRoomService.createChatRoom(command))
                    .isInstanceOf(TrainerNotFoundException.class);

            verify(chatRoomRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 ACTIVE 채팅방이 존재하면 ChatRoomAlreadyExistsException이 발생한다")
        void fail_alreadyExists() {
            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.of(new TrainerView(11L)));
            when(chatRoomRepository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(1L, 11L, 1L, ChatRoomStatus.ACTIVE)).thenReturn(true);

            assertThatThrownBy(() -> chatRoomService.createChatRoom(command))
                    .isInstanceOf(ChatRoomAlreadyExistsException.class);

            verify(chatRoomRepository, never()).save(any());
        }

        @Test
        @DisplayName("동시 요청으로 unique 제약 위반 시 ChatRoomAlreadyExistsException이 발생한다")
        void fail_uniqueConstraintViolation() {
            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.of(new TrainerView(11L)));
            when(chatRoomRepository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(1L, 11L, 1L, ChatRoomStatus.ACTIVE)).thenReturn(false);
            when(chatRoomRepository.save(any())).thenThrow(uniqueConstraintException("uk_chat_rooms_active"));

            assertThatThrownBy(() -> chatRoomService.createChatRoom(command))
                    .isInstanceOf(ChatRoomAlreadyExistsException.class);
        }

        @Test
        @DisplayName("trainer_id FK 위반 시 TrainerNotFoundException이 발생한다")
        void fail_trainerFkViolation() {
            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.of(new TrainerView(11L)));
            when(chatRoomRepository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(1L, 11L, 1L, ChatRoomStatus.ACTIVE)).thenReturn(false);
            when(chatRoomRepository.save(any())).thenThrow(uniqueConstraintException("fk_chat_rooms_trainer"));

            assertThatThrownBy(() -> chatRoomService.createChatRoom(command))
                    .isInstanceOf(TrainerNotFoundException.class);
        }

        @Test
        @DisplayName("pt_course_id FK 위반 시 PtCourseNotFoundException이 발생한다")
        void fail_ptCourseFkViolation() {
            when(trainerQueryPort.findActiveTrainer(11L)).thenReturn(Optional.of(new TrainerView(11L)));
            when(chatRoomRepository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(1L, 11L, 1L, ChatRoomStatus.ACTIVE)).thenReturn(false);
            when(chatRoomRepository.save(any())).thenThrow(uniqueConstraintException("fk_chat_rooms_pt_course"));

            assertThatThrownBy(() -> chatRoomService.createChatRoom(command))
                    .isInstanceOf(PtCourseNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("채팅방 나가기")
    class LeaveChatRoom {

        private final ChatRoom activeRoom = ChatRoom.restore(
                1L, 1L, 11L, 1L, false, false,
                ChatRoomStatus.ACTIVE, LocalDateTime.now(), null, null, LocalDateTime.now());

        private final ChatRoom deletedRoom = ChatRoom.restore(
                1L, 1L, 11L, 1L, true, true,
                ChatRoomStatus.DELETED, LocalDateTime.now(), LocalDateTime.now(), null, LocalDateTime.now());

        private final ChatRoom userLeftRoom = ChatRoom.restore(
                1L, 1L, 11L, 1L, true, false,
                ChatRoomStatus.CLOSED, LocalDateTime.now(), LocalDateTime.now(), null, LocalDateTime.now());

        @Test
        @DisplayName("존재하지 않는 채팅방이면 ChatRoomNotFoundException이 발생한다")
        void fail_chatRoomNotFound() {
            when(chatRoomRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> chatRoomService.leaveChatRoom(99L, 1L))
                    .isInstanceOf(ChatRoomNotFoundException.class);
        }

        @Test
        @DisplayName("참여자가 아닌 사용자가 나가려 하면 ChatRoomAccessDeniedException이 발생한다")
        void fail_accessDenied() {
            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(activeRoom));

            assertThatThrownBy(() -> chatRoomService.leaveChatRoom(1L, 999L))
                    .isInstanceOf(ChatRoomAccessDeniedException.class);
        }

        @Test
        @DisplayName("DELETED 상태 채팅방에서 나가려 하면 ChatRoomClosedException이 발생한다")
        void fail_alreadyClosed() {
            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(deletedRoom));

            assertThatThrownBy(() -> chatRoomService.leaveChatRoom(1L, 1L))
                    .isInstanceOf(ChatRoomClosedException.class);
        }

        @Test
        @DisplayName("이미 나간 채팅방에서 다시 나가려 하면 ChatRoomAlreadyLeftException이 발생한다")
        void fail_alreadyLeft() {
            when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(userLeftRoom));

            assertThatThrownBy(() -> chatRoomService.leaveChatRoom(1L, 1L))
                    .isInstanceOf(ChatRoomAlreadyLeftException.class);
        }
    }

    private DataIntegrityViolationException uniqueConstraintException(String constraintName) {
        ConstraintViolationException hibernateCve = new ConstraintViolationException(
                "constraint violation", new SQLException(), constraintName);
        return new DataIntegrityViolationException("constraint violation", hibernateCve);
    }
}
