package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.domain.model.Blacklist;
import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<Long> findExpiredSevenDaySuspensionUserIds(LocalDateTime now) {
        return springDataBlacklistsJpaRepository.findExpiredSevenDaySuspensionUserIds(now);
    }

    @Override
    public int releaseExpiredSevenDaySuspensions(List<Long> userIds, LocalDateTime now) {
        if (userIds.isEmpty()) {
            return 0;
        }
        return springDataBlacklistsJpaRepository.releaseExpiredSevenDaySuspensions(userIds, now);
    }
}
