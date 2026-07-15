package com.ssambbong.gymjjak.global.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final String CALENDAR_MONTH_CACHE = "calendarMonth";
    private static final String PT_MAIN_STATS_CACHE = "ptMainStats";
    private static final String PT_MAIN_POPULAR_CACHE = "ptMainPopular";
    private static final String PT_COURSE_LIST_CACHE = "ptCourseList";
    private static final String EXERCISE_LIST_CACHE = "exerciseList";
    private static final String EXERCISE_SNAPSHOT_CACHE = "exerciseSnapshot";
    private static final String MY_ONBOARDING_CACHE = "myOnboarding";
    private static final String USER_PROFILE_CACHE = "userProfile";
    private static final String USER_USERNAME_NICKNAME_CACHE =
            "userUsernameNickname";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 월간 캘린더 조회 캐시
        // 사용자별 월 단위 데이터이므로 기존 정책인 30분 TTL 유지
        cacheManager.registerCustomCache(
                CALENDAR_MONTH_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        // 메인 페이지 통계 캐시
        // 전체 사용자 공통 데이터이므로 1시간 TTL 적용
        cacheManager.registerCustomCache(
                PT_MAIN_STATS_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        // 메인 페이지 인기 PT 캐시
        // 전체 사용자 공통 데이터이므로 1시간 TTL 적용
        cacheManager.registerCustomCache(
                PT_MAIN_POPULAR_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        // PT 코스 목록 캐시
        cacheManager.registerCustomCache(
                PT_COURSE_LIST_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        // 운동 목록 캐시
        cacheManager.registerCustomCache(
                EXERCISE_LIST_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        // 캘린더 운동 스냅샷 캐시
        cacheManager.registerCustomCache(
                EXERCISE_SNAPSHOT_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(5_000)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        // 사용자 온보딩 정보 캐시
        cacheManager.registerCustomCache(
                MY_ONBOARDING_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        // 사용자 프로필 캐시
        cacheManager.registerCustomCache(
                USER_PROFILE_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .recordStats()
                        .build()
        );

        // 사용자 이메일 및 닉네임 캐시
        cacheManager.registerCustomCache(
                USER_USERNAME_NICKNAME_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        return cacheManager;
    }
}
