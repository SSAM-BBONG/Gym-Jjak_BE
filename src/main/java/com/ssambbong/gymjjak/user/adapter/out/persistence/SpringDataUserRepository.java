package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.application.result.FindBlacklistUserResult;
import com.ssambbong.gymjjak.user.application.result.FindTrainerUserResult;
import com.ssambbong.gymjjak.user.application.result.FindUserResult;
import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import com.ssambbong.gymjjak.user.domain.model.SocialProvider;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import jakarta.persistence.LockModeType;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    // 구독 상태를 변경하는 모든 흐름은 동일한 사용자 행을 잠금 기준으로 사용한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserJpaEntity u where u.id = :userId")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("userId") Long userId);

    // 교착상태 가능성을 줄이기 위해 사용자 ID 오름차순으로 한 번에 잠근다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select u
            from UserJpaEntity u
            where u.id in :userIds
            order by u.id asc
            """)
    List<UserJpaEntity> findAllByIdForUpdate(@Param("userIds") Collection<Long> userIds);

    // 아직 유효한 구독이 없는 사용자만 한 번의 쿼리로 무료 상태로 전환한다.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            update users u
            set u.is_paid = false
            where u.user_id in (:userIds)
              and u.is_paid = true
              and not exists (
                  select 1
                  from subscriptions s
                  where s.user_id = u.user_id
                    and s.status = 'ACTIVE'
                    and s.expired_at > :now
              )
            """, nativeQuery = true)
    int markUnpaidWithoutActiveSubscription(
            @Param("userIds") Collection<Long> userIds,
            @Param("now") LocalDateTime now
    );

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneAndRole(String phone, UserRole role);

    boolean existsByPhone(String phone);

    boolean existsByIdAndRole(Long userId, UserRole role);

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
        set u.status = :activeStatus
        where u.id in :userIds
          and u.status = :suspendedStatus
          and u.deletedAt is null
    """)
    int activateSevenDaySuspendedUsers(
            @Param("userIds") List<Long> userIds,
            @Param("suspendedStatus") UserStatus suspendedStatus,
            @Param("activeStatus") UserStatus activeStatus
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

    @Query(
            value = """
        select new com.ssambbong.gymjjak.user.application.result.FindUserResult(
            u.id,
            u.username,
            u.name,
            u.nickname,
            u.status
        )
        from UserJpaEntity u
        where (:keyword is null
               or trim(:keyword) = ''
               or lower(u.name) like lower(concat('%', trim(:keyword), '%')))
        """,
            countQuery = """
        select count(u)
        from UserJpaEntity u
        where (:keyword is null
               or trim(:keyword) = ''
               or lower(u.name) like lower(concat('%', trim(:keyword), '%')))
        """
    )
    Page<FindUserResult> findUsers(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(
            value = """
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
          and (:keyword is null
               or trim(:keyword) = ''
               or lower(u.name) like lower(concat('%', trim(:keyword), '%')))
        """,
            countQuery = """
        select count(u)
        from UserJpaEntity u
        join BlacklistsJpaEntity b on b.userId = u.id
        where u.status in :userStatuses
          and b.status = :blacklistStatus
          and b.deletedAt is null
          and (:keyword is null
               or trim(:keyword) = ''
               or lower(u.name) like lower(concat('%', trim(:keyword), '%')))
        """
    )
    Page<FindBlacklistUserResult> findBlacklistUsers(
            @Param("userStatuses") List<UserStatus> userStatuses,
            @Param("blacklistStatus") BlacklistStatus blacklistStatus,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<UserJpaEntity> findBySocialProviderAndSocialId(
            SocialProvider socialProvider,
            String socialId
    );

    @Query(
            value = """
        select new com.ssambbong.gymjjak.user.application.result.FindTrainerUserResult(
            tp.trainerProfileId,
            u.id,
            u.username,
            u.name,
            u.nickname,
            u.status
        )
        from UserJpaEntity u
        join TrainerProfileJpaEntity tp on tp.userId = u.id
        where u.role = :role
          and u.deletedAt is null
          and (
              :keyword is null
              or trim(:keyword) = ''
              or lower(u.name) like lower(concat('%', trim(:keyword), '%'))
          )
        order by u.id desc
        """,
            countQuery = """
        select count(u)
        from UserJpaEntity u
        join TrainerProfileJpaEntity tp on tp.userId = u.id
        where u.role = :role
          and u.deletedAt is null
          and (
              :keyword is null
              or trim(:keyword) = ''
              or lower(u.name) like lower(concat('%', trim(:keyword), '%'))
          )
        """
    )
    Page<FindTrainerUserResult> findTrainerUsers(
            @Param("role") UserRole role,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // dashboard : 월별 사용자 수
    @Query(
            value = """
            select date_format(u.created_at, '%Y-%m') as month,
                   count(*) as count
            from users u
            where u.deleted_at is null
              and u.created_at >= :startDate
              and u.created_at < :endDate
            group by date_format(u.created_at, '%Y-%m')
            order by month asc
            """,
            nativeQuery = true
    )
    List<MonthlyUserSignupRow> findMonthlyUserSignups(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // dashboard : 전체 USER 일반 이용자 수
    @Query("""
            select count(u)
            from UserJpaEntity u
            where u.role = :role
              and u.deletedAt is null
            """)
    long countActiveUsersByRole(@Param("role") UserRole role);

    // dashboard : 월별 사용자 내부 인터페이스
    interface MonthlyUserSignupRow {
        String getMonth();
        Long getCount();
    }


}
