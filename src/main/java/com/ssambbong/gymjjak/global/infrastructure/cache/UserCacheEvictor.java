package com.ssambbong.gymjjak.global.infrastructure.cache;

import com.ssambbong.gymjjak.user.application.port.out.UserCacheEvictionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCacheEvictor implements UserCacheEvictionPort {

    private static final String USER_PROFILE_CACHE = "userProfile";
    private static final String USER_USERNAME_NICKNAME_CACHE = "userUsernameNickname";

    private final CacheManager cacheManager;

    @Override
    public void evictUserProfile(Long userId) {
        evict(USER_PROFILE_CACHE, userId);
    }

    @Override
    public void evictUsernameAndNickname(Long userId) {
        evict(USER_USERNAME_NICKNAME_CACHE, userId);
    }

    private void evict(
            String cacheName,
            Long userId
    ) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
