package com.ssambbong.gymjjak.global.infrastructure.cache;

import com.ssambbong.gymjjak.exercise.application.port.out.ExerciseCacheEvictionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExerciseCacheEvictor implements ExerciseCacheEvictionPort {

    private static final String EXERCISE_LIST_CACHE = "exerciseList";
    private static final String EXERCISE_SNAPSHOT_CACHE = "exerciseSnapshot";

    private final CacheManager cacheManager;

    @Override
    public void evictExerciseList() {
        clear(EXERCISE_LIST_CACHE);
    }

    @Override
    public void evictExerciseSnapshots() {
        clear(EXERCISE_SNAPSHOT_CACHE);
    }

    private void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
