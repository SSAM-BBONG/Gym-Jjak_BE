package com.ssambbong.gymjjak.global.infrastructure.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.calendar.application.result.CalendarExerciseSnapshot;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase.PopularCourseView;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase.PtCourseListView;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase.PtStatsView;
import com.ssambbong.gymjjak.user.application.result.UserProfileResult;
import com.ssambbong.gymjjak.user.application.result.UserUsernameAndNicknameResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final String CALENDAR_MONTH_CACHE = "calendarMonth";
    private static final String PT_MAIN_STATS_CACHE = "ptMainStats";
    private static final String PT_MAIN_POPULAR_CACHE = "ptMainPopular";
    private static final String PT_COURSE_LIST = "ptCourseList";
    private static final String EXERCISE_LIST_CACHE = "exerciseList";
    private static final String EXERCISE_SNAPSHOT_CACHE = "exerciseSnapshot";
    private static final String MY_ONBOARDING_CACHE = "myOnboarding";
    private static final String USER_PROFILE_CACHE = "userProfile";
    private static final String USER_USERNAME_NICKNAME_CACHE = "userUsernameNickname";

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            @Value("${app.redis.key-prefix:gymjjak:local}") String keyPrefix
    ) {
        RedisCacheConfiguration defaultConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(RedisSerializer.string())
                        )
                        .disableCachingNullValues()
                        .computePrefixWith(
                                cacheName -> keyPrefix + ":cache:" + cacheName + "::"
                        )
                        .entryTtl(Duration.ofMinutes(10));

        Map<String, RedisCacheConfiguration> cacheConfigurations =
                Map.ofEntries(
                        Map.entry(
                                CALENDAR_MONTH_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofMinutes(30))
                                        .serializeValuesWith(jsonPair(objectMapper, CalendarMonthResult.class))
                        ),
                        Map.entry(
                                PT_MAIN_STATS_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofHours(1))
                                        .serializeValuesWith(jsonPair(objectMapper, PtStatsView.class))
                        ),
                        Map.entry(
                                PT_MAIN_POPULAR_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofHours(1))
                                        .serializeValuesWith(jsonListPair(objectMapper, PopularCourseView.class))
                        ),
                        Map.entry(
                                PT_COURSE_LIST,
                                defaultConfiguration
                                        .entryTtl(Duration.ofMinutes(30))
                                        .serializeValuesWith(jsonListPair(objectMapper, PtCourseListView.class))
                        ),
                        Map.entry(
                                EXERCISE_LIST_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofHours(1))
                                        .serializeValuesWith(jsonListPair(objectMapper, ExerciseResult.class))
                        ),
                        Map.entry(
                                EXERCISE_SNAPSHOT_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofHours(1))
                                        .serializeValuesWith(jsonPair(objectMapper, CalendarExerciseSnapshot.class))
                        ),
                        Map.entry(
                                MY_ONBOARDING_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofMinutes(30))
                                        .serializeValuesWith(jsonPair(objectMapper, MyOnboardingResult.class))
                        ),
                        Map.entry(
                                USER_PROFILE_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofMinutes(10))
                                        .serializeValuesWith(jsonPair(objectMapper, UserProfileResult.class))
                        ),
                        Map.entry(
                                USER_USERNAME_NICKNAME_CACHE,
                                defaultConfiguration
                                        .entryTtl(Duration.ofMinutes(30))
                                        .serializeValuesWith(jsonPair(objectMapper, UserUsernameAndNicknameResult.class))
                        )
                );

        RedisCacheWriter cacheWriter =
                RedisCacheWriter.nonLockingRedisCacheWriter(
                        connectionFactory,
                        BatchStrategies.scan(1_000)
                );

        return RedisCacheManager.builder(cacheWriter)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .enableStatistics()
                .build();
    }

    private static <T> RedisSerializationContext.SerializationPair<T> jsonPair(
            ObjectMapper objectMapper,
            Class<T> type
    ) {
        Jackson2JsonRedisSerializer<T> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper.copy(), type);

        return RedisSerializationContext.SerializationPair.fromSerializer(serializer);
    }

    private static <E> RedisSerializationContext.SerializationPair<List<E>> jsonListPair(
            ObjectMapper objectMapper,
            Class<E> elementType
    ) {
        JavaType listType = objectMapper
                .getTypeFactory()
                .constructCollectionType(ArrayList.class, elementType);

        Jackson2JsonRedisSerializer<List<E>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper.copy(), listType);

        return RedisSerializationContext.SerializationPair.fromSerializer(serializer);
    }
}
