package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSuspensionExpirationServiceTest {

    @Mock
    private BlacklistPort blacklistPort;

    @Mock
    private UserPort userPort;

    @Test
    void releasesExpiredSevenDaySuspensionsAndActivatesUsers() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 21, 12, 0);
        List<Long> userIds = List.of(1L, 2L);
        UserSuspensionExpirationService service =
                new UserSuspensionExpirationService(blacklistPort, userPort);

        when(blacklistPort.findExpiredSevenDaySuspensionUserIds(now)).thenReturn(userIds);
        when(userPort.activateExpiredSevenDaySuspendedUsers(userIds)).thenReturn(2);
        when(blacklistPort.releaseExpiredSevenDaySuspensions(userIds, now)).thenReturn(2);

        RetentionJobResult result = service.releaseExpiredSuspensions(now);

        assertThat(result.jobName()).isEqualTo("user-suspension-expiration");
        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.deletedChildCount()).isEqualTo(2);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        InOrder order = inOrder(userPort, blacklistPort);
        order.verify(userPort).activateExpiredSevenDaySuspendedUsers(userIds);
        order.verify(blacklistPort).releaseExpiredSevenDaySuspensions(userIds, now);
    }

    @Test
    void doesNothingWhenNoSuspensionHasExpired() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 21, 12, 0);
        UserSuspensionExpirationService service =
                new UserSuspensionExpirationService(blacklistPort, userPort);

        when(blacklistPort.findExpiredSevenDaySuspensionUserIds(now)).thenReturn(List.of());

        RetentionJobResult result = service.releaseExpiredSuspensions(now);

        assertThat(result.candidateCount()).isZero();
        verify(userPort, never()).activateExpiredSevenDaySuspendedUsers(List.of());
        verify(blacklistPort, never()).releaseExpiredSevenDaySuspensions(List.of(), now);
    }
}
