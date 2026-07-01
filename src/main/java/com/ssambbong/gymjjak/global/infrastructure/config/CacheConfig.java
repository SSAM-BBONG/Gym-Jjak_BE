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
    private static final String PT_COURSE_LIST = "ptCourseList";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 월간 캘린더 조회 캐시: 사용자별 월 단위 데이터라 기존 정책인 30분 TTL 유지
        cacheManager.registerCustomCache(
                CALENDAR_MONTH_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        // 메인페이지 통계 캐시: 전체 사용자 공통 데이터이므로 1시간 TTL 적용
        cacheManager.registerCustomCache(
                PT_MAIN_STATS_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        // 메인페이지 인기 PT 캐시: 전체 사용자 공통 데이터이므로 1시간 TTL 적용
        cacheManager.registerCustomCache(
                PT_MAIN_POPULAR_CACHE,
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofHours(1))
                        .recordStats()
                        .build()
        );

        cacheManager.registerCustomCache(
                PT_COURSE_LIST,
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build()
        );

        return cacheManager;
    }
}