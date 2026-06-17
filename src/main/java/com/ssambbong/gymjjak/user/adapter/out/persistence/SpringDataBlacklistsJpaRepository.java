package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
