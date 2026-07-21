package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.user.application.port.in.ReleaseExpiredSuspensionsUseCase;
import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSuspensionExpirationService implements ReleaseExpiredSuspensionsUseCase {

    private static final String JOB_NAME = "user-suspension-expiration";

    private final BlacklistPort blacklistPort;
    private final UserPort userPort;

    @Override
    public RetentionJobResult releaseExpiredSuspensions(LocalDateTime now) {
        List<Long> userIds = blacklistPort.findExpiredSevenDaySuspensionUserIds(now);
        if (userIds.isEmpty()) {
            return RetentionJobResult.empty(JOB_NAME);
        }

        int activatedUsers = userPort.activateExpiredSevenDaySuspendedUsers(userIds);
        int releasedBlacklists = blacklistPort.releaseExpiredSevenDaySuspensions(userIds, now);

        return new RetentionJobResult(JOB_NAME, userIds.size(), releasedBlacklists, activatedUsers);
    }
}
