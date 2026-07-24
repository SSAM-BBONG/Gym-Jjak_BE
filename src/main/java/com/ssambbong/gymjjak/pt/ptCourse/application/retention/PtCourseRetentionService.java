package com.ssambbong.gymjjak.pt.ptCourse.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtCourseRetentionService {

    public static final String JOB_NAME = "pt-course-retention"; // Job 클래스에서 참조하는 이름 상수

    private final PtCourseRetentionProperties properties;
    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMediaRepository feedbackMediaRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredPtCourses(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        // 소프트딜리트 > periodDays PT ID 배치 조회
        List<Long> candidateIds = ptCourseRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=pt-course-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        // 자식 테이블 먼저 삭제 (피드백미디어 → 피드백 → 커리큘럼 → 스케줄 → PT 강습)
        List<Long> feedbackIds = feedbackRepository.findIdsByPtCourseIds(candidateIds);
        if (!feedbackIds.isEmpty()) {
            feedbackMediaRepository.hardDeleteByFeedbackIds(feedbackIds);
            feedbackRepository.hardDeleteByIds(feedbackIds);
        }
        int deletedCurriculums = ptCurriculumRepository.hardDeleteByPtCourseIds(candidateIds);
        int deletedSchedules = ptCourseScheduleRepository.hardDeleteByPtCourseIds(candidateIds);
        int deletedCourses = ptCourseRepository.hardDeleteByIds(candidateIds);

        log.info("event=pt-course-retention-completed threshold={}, candidateCount={}, deletedCurriculums={}, deletedSchedules={}, deletedCourses={}",
                threshold, candidateIds.size(), deletedCurriculums, deletedSchedules, deletedCourses);

        // deletedChildCount = 커리큘럼 + 스케줄 합산
        return new RetentionJobResult(JOB_NAME, candidateIds.size(), deletedCurriculums + deletedSchedules, deletedCourses);
    }
}
