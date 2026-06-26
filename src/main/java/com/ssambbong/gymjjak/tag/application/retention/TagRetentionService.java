package com.ssambbong.gymjjak.tag.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagRetentionService {

    public static final String JOB_NAME = "tag-retention"; // Job 클래스에서 참조하는 이름 상수

    private final TagRetentionProperties properties;
    private final TagRepository tagRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredTags(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        // 소프트딜리트된 지 periodDays 초과한 태그 ID 배치 조회
        List<Long> candidateIds = tagRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=tag-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        // 하드딜리트 실행
        int deleted = tagRepository.hardDeleteByIds(candidateIds);

        log.info("event=tag-retention-completed threshold={}, candidateCount={}, deleted={}",
                threshold, candidateIds.size(), deleted);

        // 태그는 자식 테이블 없음 → deletedChildCount=0
        return new RetentionJobResult(JOB_NAME, candidateIds.size(), 0, deleted);
    }
}
