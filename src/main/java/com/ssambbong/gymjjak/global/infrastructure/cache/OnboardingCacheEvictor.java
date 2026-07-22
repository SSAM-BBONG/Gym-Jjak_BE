package com.ssambbong.gymjjak.global.infrastructure.cache;

import com.ssambbong.gymjjak.onboarding.application.port.out.OnboardingCacheEvictionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnboardingCacheEvictor implements OnboardingCacheEvictionPort {

    private static final String MY_ONBOARDING_CACHE = "myOnboarding";

    private final CacheManager cacheManager;

    @Override
    public void evictMyOnboarding(Long userId) {
        Cache cache = cacheManager.getCache(MY_ONBOARDING_CACHE);
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
