package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
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
                chatRoom.getTrainerProfileId(),
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
    public boolean existsByUserIdAndTrainerProfileIdAndStatus(Long userId, Long trainerProfileId, ChatRoomStatus status) {
        return repository.existsByUserIdAndTrainerProfileIdAndStatus(userId, trainerProfileId, status);
    }

    private ChatRoom toDomain(ChatRoomJpaEntity entity) {
        return ChatRoom.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTrainerProfileId(),
                entity.getPtCourseId(),
                entity.isUserLeft(),
                entity.isTrainerLeft(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getClosedAt(),
                entity.getUpdatedAt()
        );
    }
}
