package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryAdapter implements ChatRoomRepository {

    private final SpringDataChatRoomRepository repository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        ChatRoomJpaEntity entity = new ChatRoomJpaEntity(
                chatRoom.getUserId(),
                chatRoom.getTrainerId(),
                chatRoom.getPtCourseId(),
                chatRoom.getStatus()
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(Long userId, Long trainerId, Long ptCourseId, ChatRoomStatus status) {
        return repository.existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(userId, trainerId, ptCourseId, status);
    }

    @Override
    public void leaveChatRoom(ChatRoom chatRoom) {
        ChatRoomJpaEntity entity = repository.findById(chatRoom.getId())
                .orElseThrow(ChatRoomNotFoundException::new);
        if (chatRoom.isUserLeft()) entity.markUserLeft();
        if (chatRoom.isTrainerLeft()) entity.markTrainerLeft();
        if (chatRoom.getStatus() == ChatRoomStatus.CLOSED) {
            entity.close(chatRoom.getClosedAt());
        }
    }

    private ChatRoom toDomain(ChatRoomJpaEntity entity) {
        return ChatRoom.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTrainerId(),
                entity.getPtCourseId(),
                entity.isUserLeft(),
                entity.isTrainerLeft(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getClosedAt(),
                entity.getLastMessageAt(),
                entity.getUpdatedAt()
        );
    }
}
