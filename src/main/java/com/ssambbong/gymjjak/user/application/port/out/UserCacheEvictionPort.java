package com.ssambbong.gymjjak.user.application.port.out;

public interface UserCacheEvictionPort {

    void evictUserProfile(Long userId);

    void evictUsernameAndNickname(Long userId);
}
