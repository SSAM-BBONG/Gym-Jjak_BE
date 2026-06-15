package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByIdAndRoleAndStatusAndDeletedAtIsNull(Long id, UserRole role, UserStatus status);

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
    set u.deletedAt = :deletedAt,
        u.updatedAt = :deletedAt
    where u.id = :userId
      and u.deletedAt is null
""")
    void withdraw(
            @Param("userId") Long userId,
            @Param("deletedAt") LocalDateTime deletedAt
    );

}
