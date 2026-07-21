package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataBlacklistsJpaRepository extends JpaRepository<BlacklistsJpaEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update BlacklistsJpaEntity b
        set b.status = :releasedStatus
        where b.userId = :userId
          and b.status = :activeStatus
          and b.deletedAt is null
    """)
    void releaseActiveBlacklistsByUserId(
            @Param("userId") Long userId,
            @Param("activeStatus") BlacklistStatus activeStatus,
            @Param("releasedStatus") BlacklistStatus releasedStatus
    );

    @Query("""
        select distinct b.userId
        from BlacklistsJpaEntity b
        where b.type = com.ssambbong.gymjjak.user.domain.model.BlacklistType.DAY_7
          and b.status = com.ssambbong.gymjjak.user.domain.model.BlacklistStatus.ACTIVE
          and b.endedAt is not null
          and b.endedAt <= :now
          and b.deletedAt is null
    """)
    List<Long> findExpiredSevenDaySuspensionUserIds(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update BlacklistsJpaEntity b
        set b.status = com.ssambbong.gymjjak.user.domain.model.BlacklistStatus.RELEASED
        where b.userId in :userIds
          and b.type = com.ssambbong.gymjjak.user.domain.model.BlacklistType.DAY_7
          and b.status = com.ssambbong.gymjjak.user.domain.model.BlacklistStatus.ACTIVE
          and b.endedAt is not null
          and b.endedAt <= :now
          and b.deletedAt is null
    """)
    int releaseExpiredSevenDaySuspensions(
            @Param("userIds") List<Long> userIds,
            @Param("now") LocalDateTime now
    );
}
