package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.domain.model.Blacklist;

public interface BlacklistPort {

    void releaseActiveBlacklistsByUserId(Long userId);

    Blacklist save(Blacklist blacklist);
}
