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
import java.util.Objects;

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
    // 트레이너 대시보드 조회.
    public TrainerMainPageResult findMainPage(Long userId) {
        // 트레이너 profileId 조회
        TrainerProfile trainerProfile = findActiveTrainerProfile(userId);

        // 소속 조직 수 조회
        long organizationCount = organizationQueryPort
                .countActiveOrganizations(trainerProfile.getTrainerProfileId());
        // 수강생 수 조회
        long currentStudentCount = ptQueryPort
                .countCurrentStudents(trainerProfile.getTrainerProfileId());
        // 진행 중인 pt 조회
        List<TrainerMainPtQueryPort.InProgressPtCourse> courses = ptQueryPort
                // 상위 4개 조회
                .findTopCoursesByCurrentStudentCount(trainerProfile.getTrainerProfileId(), TOP_PT_COURSE_LIMIT);

        // 인기 pt 4개 각 헬스장명 조회
        Map<Long, String> organizationNameById = findOrganizationNames(courses);
        // 인기 pt 4개 각 섬네일 이미지 조회
        Map<Long, FileUrlResult> thumbnailByFileId = findThumbnailUrls(courses);

        // 카드에서 조회할 항목으로 변환
        List<TrainerMainPageResult.InProgressPtCourse> cards = courses.stream()
                .map(course -> new TrainerMainPageResult.InProgressPtCourse(
                        course.ptCourseId(),
                        resolveThumbnailUrl(course.thumbnailFileId(), thumbnailByFileId),
                        course.title(),
                        trainerProfile.getTrainerName(),
                        resolveOrganizationName(course.organizationId(), organizationNameById),
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

    // pt 내부 조직id 추출 후 조직명 찾기
    private Map<Long, String> findOrganizationNames(
            List<TrainerMainPtQueryPort.InProgressPtCourse> courses
    ) {
        List<Long> organizationIds = courses.stream()
                .map(TrainerMainPtQueryPort.InProgressPtCourse::organizationId)
                .filter(Objects::nonNull)
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
                .filter(Objects::nonNull)
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

    // 소속 조직이 없는 강습은 null 키 조회 없이 조직명을 비워 반환합니다.
    private String resolveOrganizationName(Long organizationId, Map<Long, String> organizationNameById) {
        if (organizationId == null) {
            return null;
        }

        return organizationNameById.get(organizationId);
    }
}
