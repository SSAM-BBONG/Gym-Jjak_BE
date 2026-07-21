package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.domain.model.Blacklist;

import java.time.LocalDateTime;
import java.util.List;

public interface BlacklistPort {

    void releaseActiveBlacklistsByUserId(Long userId);

    Blacklist save(Blacklist blacklist);

    List<Long> findExpiredSevenDaySuspensionUserIds(LocalDateTime now);

    int releaseExpiredSevenDaySuspensions(List<Long> userIds, LocalDateTime now);
}
