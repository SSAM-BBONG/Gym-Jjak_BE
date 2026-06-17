package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.application.result.FindBlacklistUserResult;
import com.ssambbong.gymjjak.user.application.result.FindUserResult;
import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByNicknameAndIdNot(String nickname, Long userId);

    boolean existsByPhoneAndIdNot(String phone, Long userId);

    @Modifying
    @Query("""
        update UserJpaEntity u
        set u.lastLoginAt = :lastLoginAt
        where u.id = :userId
        """)
    void updateLastLoginAt(
            @Param("userId") Long userId,
            @Param("lastLoginAt") LocalDateTime lastLoginAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
update UserJpaEntity u
set u.status = :status,
    u.deletedAt = :deletedAt,
    u.updatedAt = :deletedAt
where u.id = :userId
  and u.deletedAt is null
""")
    int withdraw(
            @Param("userId") Long userId,
            @Param("status") UserStatus status,
            @Param("deletedAt") LocalDateTime deletedAt
    );

    @Query("""
        select count(u)
        from UserJpaEntity u
        where u.deletedAt is not null
          and u.deletedAt <= :threshold
    """)
    int countWithdrawnUsersBefore(@Param("threshold") LocalDateTime threshold);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = """
            delete from users
            where deleted_at is not null
              and deleted_at <= :threshold
            order by deleted_at asc
            limit :batchSize
        """,
            nativeQuery = true
    )
    int deleteWithdrawnUsersBefore(
            @Param("threshold") LocalDateTime threshold,
            @Param("batchSize") int batchSize
    );

    @Modifying
    @Query("""
    update UserJpaEntity u
    set u.status = :status
    where u.id = :userId
""")
    void updateStatus(
            @Param("userId") Long userId,
            @Param("status") UserStatus status
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
update UserJpaEntity u
set u.password = :encodedPassword,
    u.updatedAt = :updatedAt
where u.id = :userId
  and u.deletedAt is null
""")
    int changePassword(
            @Param("userId") Long userId,
            @Param("encodedPassword") String encodedPassword,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Query("""
select new com.ssambbong.gymjjak.user.application.result.FindUserResult(
    u.id,
    u.username,
    u.name,
    u.nickname,
    u.status
)
from UserJpaEntity u
where (:name is null
       or trim(:name) = ''
       or lower(u.name) like lower(concat('%', trim(:name), '%')))
  and (:cursor is null or u.id < :cursor)
order by u.id desc
""")
    List<FindUserResult> findUsersByCursor(
            @Param("name") String name,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("""
select new com.ssambbong.gymjjak.user.application.result.FindBlacklistUserResult(
    u.id,
    u.username,
    u.name,
    u.nickname,
    u.status,
    b.type,
    b.reason
)
from UserJpaEntity u
join BlacklistsJpaEntity b on b.userId = u.id
where u.status in :userStatuses
  and b.status = :blacklistStatus
  and b.deletedAt is null
  and (:name is null
       or trim(:name) = ''
       or lower(u.name) like lower(concat('%', trim(:name), '%')))
  and (:cursor is null or u.id < :cursor)
order by u.id desc
""")
    List<FindBlacklistUserResult> findBlacklistUsersByCursor(
            @Param("userStatuses") List<UserStatus> userStatuses,
            @Param("blacklistStatus") BlacklistStatus blacklistStatus,
            @Param("name") String name,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
