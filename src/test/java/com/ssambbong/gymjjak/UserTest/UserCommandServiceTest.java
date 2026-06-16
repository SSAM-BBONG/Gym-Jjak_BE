package com.ssambbong.gymjjak.UserTest;

import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.application.service.UserCommandService;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import com.ssambbong.gymjjak.user.domain.policy.UserPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserCommandServiceTest {

    private TokenPort tokenPort;
    private UserPort userPort;
    private BlacklistPort blacklistPort;
    private UserPolicy userPolicy;
    private UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        userPort = mock(UserPort.class);
        tokenPort = mock(TokenPort.class);
        userPolicy = mock(UserPolicy.class);

        userCommandService = new UserCommandService(
                userPort,
                tokenPort,
                blacklistPort
        );
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void registerUser_success() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
                "test1234@test.com",
                "Test1234!",
                "서주원",
                "운동왕",
                "010-1111-2222"
        );

        when(userPort.existsByUsername(command.username()))
                .thenReturn(false);
        when(userPort.existsByNickname(command.nickname()))
                .thenReturn(false);
        when(userPort.existsByPhone(command.phone()))
                .thenReturn(false);

        when(userPort.encode(command.password()))
                .thenReturn("encodedPassword");

        when(userPort.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userCommandService.registerUser(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userPort).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("test1234@test.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getName()).isEqualTo("서주원");
        assertThat(savedUser.getNickname()).isEqualTo("운동왕");
        assertThat(savedUser.getPhone()).isEqualTo("010-1111-2222");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(savedUser.getCreatedAt()).isEqualTo(Instant.parse("2026-05-27T00:00:00Z"));
        assertThat(savedUser.getUpdatedAt()).isEqualTo(Instant.parse("2026-05-27T00:00:00Z"));
        assertThat(savedUser.getLastLoginAt()).isNull();
        assertThat(savedUser.getDeletedAt()).isNull();

        verify(userPort).encode("Test1234!");
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 회원가입에 실패한다")
    void registerUser_fail_duplicateUsername() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
                "test1234@test.com",
                "Test1234!",
                "서주원",
                "운동왕",
                "010-1111-2222"
        );

        when(userPort.existsByUsername(command.username()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userCommandService.registerUser(command))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.DUPLICATE_USERNAME.getMessage());

        verify(userPort, never()).save(any(User.class));
        verify(userPort, never()).encode(anyString());
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임이면 회원가입에 실패한다")
    void registerUser_fail_duplicateNickname() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
                "newuser@test.com",
                "Test1234!",
                "서주원",
                "운동왕",
                "010-1111-2222"
        );

        when(userPort.existsByUsername(command.username()))
                .thenReturn(false);
        when(userPort.existsByNickname(command.nickname()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userCommandService.registerUser(command))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.DUPLICATE_NICKNAME.getMessage());

        verify(userPort, never()).save(any(User.class));
        verify(userPort, never()).encode(anyString());
    }

    @Test
    @DisplayName("이미 사용 중인 전화번호면 회원가입에 실패한다")
    void registerUser_fail_duplicatePhone() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
                "newuser@test.com",
                "Test1234!",
                "서주원",
                "새닉네임",
                "010-1111-2222"
        );

        when(userPort.existsByUsername(command.username()))
                .thenReturn(false);
        when(userPort.existsByNickname(command.nickname()))
                .thenReturn(false);
        when(userPort.existsByPhone(command.phone()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userCommandService.registerUser(command))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.DUPLICATE_PHONE.getMessage());

        verify(userPort, never()).save(any(User.class));
        verify(userPort, never()).encode(anyString());
    }
}
