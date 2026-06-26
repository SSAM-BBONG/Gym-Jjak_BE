package com.ssambbong.gymjjak.category.application.retention;

import com.ssambbong.gymjjak.category.domain.repository.CategoryRepository;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryRetentionService {

    public static final String JOB_NAME = "category-retention";
    private final CategoryRetentionProperties properties;
    private final CategoryRepository categoryRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredCategories(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        // 삭제된 날 > periodDays 카테고리 ID 배치 조회
        List<Long> candidateIds = categoryRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());
        // 위가 0이라면
        if (candidateIds.isEmpty()) {
            log.info("event=category-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        // 하드딜리트
        int deleted = categoryRepository.hardDeleteByIds(candidateIds);
        log.info("event=category-retention-completed threshold={}, candidateCount={}, deleted={}", threshold, candidateIds.size(), deleted);

        // 카테고리는 자식 테이블 없음
        return new RetentionJobResult(JOB_NAME, candidateIds.size(), 0, deleted);

    }
}
