package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.domain.model.Blacklist;
import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlacklistAdapter implements BlacklistPort {

    private final SpringDataBlacklistsJpaRepository springDataBlacklistsJpaRepository;
    private final BlacklistPersistenceMapper blacklistPersistenceMapper;

    @Override
    public void releaseActiveBlacklistsByUserId(Long userId) {
        springDataBlacklistsJpaRepository.releaseActiveBlacklistsByUserId(
                userId,
                BlacklistStatus.ACTIVE,
                BlacklistStatus.RELEASED
        );
    }

    @Override
    public Blacklist save(Blacklist blacklist) {
        BlacklistsJpaEntity entity = blacklistPersistenceMapper.toEntity(blacklist);
        BlacklistsJpaEntity savedEntity = springDataBlacklistsJpaRepository.save(entity);

        return blacklistPersistenceMapper.toDomain(savedEntity);
    }
}