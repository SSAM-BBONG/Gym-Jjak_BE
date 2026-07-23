package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.community.application.port.out.CommunityPort;
import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.application.port.out.UserCacheEvictionPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAvailabilityServiceTest {

    @Mock private UserPort userPort;
    @Mock private TokenPort tokenPort;
    @Mock private BlacklistPort blacklistPort;
    @Mock private UserCacheEvictionPort userCacheEvictionPort;
    @Mock private CommunityPort communityPort;
    @InjectMocks private UserCommandService service;

    @Test
    void 회원가입_이메일이_중복되지_않으면_사용_가능하다() {
        assertThat(service.isEmailAvailable(" test@example.com ")).isTrue();
        verify(userPort).existsByUsername("test@example.com");
    }

    @Test
    void 회원가입_닉네임이_중복되면_사용할_수_없다() {
        when(userPort.existsByNickname("짐짝이")).thenReturn(true);

        assertThat(service.isNicknameAvailable("짐짝이")).isFalse();
    }

    @Test
    void 회원가입_전화번호는_역할과_무관하게_전체_회원에서_확인한다() {
        when(userPort.existsByPhone("010-1234-5678")).thenReturn(true);

        assertThat(service.isPhoneAvailable("010-1234-5678")).isFalse();
        verify(userPort).existsByPhone("010-1234-5678");
    }

    @Test
    void 프로필_닉네임은_현재_사용자를_제외하고_확인한다() {
        assertThat(service.isNicknameAvailableForUser("현재닉네임", 10L)).isTrue();
        verify(userPort).existsByNicknameAndIdNot("현재닉네임", 10L);
    }

    @Test
    void 프로필_전화번호를_다른_회원이_사용하면_사용할_수_없다() {
        when(userPort.existsByPhoneAndIdNot("010-9999-9999", 10L)).thenReturn(true);

        assertThat(service.isPhoneAvailableForUser("010-9999-9999", 10L)).isFalse();
    }
}
