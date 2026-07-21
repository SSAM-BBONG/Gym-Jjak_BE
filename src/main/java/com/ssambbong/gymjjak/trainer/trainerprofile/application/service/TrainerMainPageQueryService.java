package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainOrganizationQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainPtQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerMainPageQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerMainPageQueryService implements TrainerMainPageQueryUseCase {

    private static final int TOP_PT_COURSE_LIMIT = 4;

    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerMainOrganizationQueryPort organizationQueryPort;
    private final TrainerMainPtQueryPort ptQueryPort;
    private final FileUrlUseCase fileUrlUseCase;

    @Override
    // trainerprofile 도메인을 기준으로 대시보드 조회 결과를 조합합니다.
    public TrainerMainPageResult findMainPage(Long userId) {
        TrainerProfile trainerProfile = findActiveTrainerProfile(userId);

        long organizationCount = organizationQueryPort
                .countActiveOrganizations(trainerProfile.getTrainerProfileId());
        long currentStudentCount = ptQueryPort
                .countCurrentStudents(trainerProfile.getTrainerProfileId());
        List<TrainerMainPtQueryPort.InProgressPtCourse> courses = ptQueryPort
                .findTopCoursesByCurrentStudentCount(trainerProfile.getTrainerProfileId(), TOP_PT_COURSE_LIMIT);

        Map<Long, String> organizationNameById = findOrganizationNames(courses);
        Map<Long, FileUrlResult> thumbnailByFileId = findThumbnailUrls(courses);

        List<TrainerMainPageResult.InProgressPtCourse> cards = courses.stream()
                .map(course -> new TrainerMainPageResult.InProgressPtCourse(
                        course.ptCourseId(),
                        resolveThumbnailUrl(course.thumbnailFileId(), thumbnailByFileId),
                        course.title(),
                        trainerProfile.getTrainerName(),
                        organizationNameById.get(course.organizationId()),
                        course.price(),
                        course.currentStudentCount()
                ))
                .toList();

        return new TrainerMainPageResult(
                organizationCount,
                currentStudentCount,
                trainerProfile.getAverageRating(),
                trainerProfile.getReviewCount(),
                cards
        );
    }

    private TrainerProfile findActiveTrainerProfile(Long userId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new TrainerProfileNotFoundException("userId", userId));

        if (trainerProfile.getStatus() != TrainerProfileStatus.ACTIVE) {
            throw new TrainerProfileNotFoundException("userId", userId);
        }

        return trainerProfile;
    }

    private Map<Long, String> findOrganizationNames(
            List<TrainerMainPtQueryPort.InProgressPtCourse> courses
    ) {
        List<Long> organizationIds = courses.stream()
                .map(TrainerMainPtQueryPort.InProgressPtCourse::organizationId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        if (organizationIds.isEmpty()) {
            return Map.of();
        }

        return organizationQueryPort.findOrganizationNamesByIds(organizationIds);
    }

    private Map<Long, FileUrlResult> findThumbnailUrls(
            List<TrainerMainPtQueryPort.InProgressPtCourse> courses
    ) {
        List<Long> thumbnailFileIds = courses.stream()
                .map(TrainerMainPtQueryPort.InProgressPtCourse::thumbnailFileId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        if (thumbnailFileIds.isEmpty()) {
            return Map.of();
        }

        try {
            return fileUrlUseCase.getUrls(thumbnailFileIds, null, false);
        } catch (RuntimeException exception) {
            log.warn("event=trainer_main_thumbnail_urls_find_failed fileIds={}", thumbnailFileIds, exception);
            return Map.of();
        }
    }

    private String resolveThumbnailUrl(Long thumbnailFileId, Map<Long, FileUrlResult> thumbnailByFileId) {
        if (thumbnailFileId == null) {
            return null;
        }

        FileUrlResult fileUrlResult = thumbnailByFileId.get(thumbnailFileId);
        return fileUrlResult == null ? null : fileUrlResult.url();
    }
}
