package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtCourseEnrichQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtCourseQueryService implements PtCourseQueryUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final PtCourseEnrichQueryPort enrichQueryPort;

    @Override
    public List<PtCourseListView> findAllPtCourses() {
        log.debug("[PtCourseList] 목록 조회 시작");

        Map<Long, String> categoryMap = buildCategoryMap();
        List<PtCourseListView> result = ptCourseRepository.findAllVisible().stream()
                .map(ptCourse -> toListView(ptCourse, categoryMap))
                .toList();

        log.info("[PtCourseList] 조회된 PT 강습 수={}", result.size());
        return result;
    }

    @Override
    public PtCourseDetailView findPtCourseDetail(Long ptCourseId) {
        log.debug("[PtCourseDetail] ptCourseId={}", ptCourseId);

        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        if (ptCourse.getStatus() != PtCourseStatus.VISIBLE) {
            throw new PtCourseNotFoundException();
        }

        log.info("[PtCourseDetail] ptCourseId={} 조회 완료", ptCourseId);
        return toDetailView(ptCourse);
    }

    // categoryId -> categoryName 매핑
    private Map<Long, String> buildCategoryMap() {
        return categoryQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        CategoryQueryUseCase.CategoryView::categoryId,
                        CategoryQueryUseCase.CategoryView::name
                ));
    }

    // ptCourse + enrich(조직/트레이너) -> 목록 응답용 View 변환
    private PtCourseListView toListView(PtCourse ptCourse, Map<Long, String> categoryMap) {
        PtCourseEnrichQueryPort.OrganizationInfo org =
                enrichQueryPort.findOrganizationById(ptCourse.getOrganizationId());
        PtCourseEnrichQueryPort.TrainerDisplayInfo trainer =
                enrichQueryPort.findTrainerProfileById(ptCourse.getTrainerProfileId());

        return new PtCourseListView(
                ptCourse.getId(),
                ptCourse.getTitle(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getPrice(),
                ptCourse.getTagId(),
                null, // TODO: TagQueryUseCase 연동 후 tagName 채우기
                ptCourse.getCategoryId(),
                categoryMap.getOrDefault(ptCourse.getCategoryId(), null),
                trainer.trainerName(),
                org.organizationId(),
                org.businessName(),
                org.roadAddress(),
                org.latitude(),
                org.longitude(),
                trainer.reviewCount()
        );
    }

    // ptCourse + enrich + 커리큘럼/스케쥴 목록 -> 상세 응답용 View 반환
    private PtCourseDetailView toDetailView(PtCourse ptCourse) {
        PtCourseEnrichQueryPort.TrainerDisplayInfo trainer =
                enrichQueryPort.findTrainerProfileById(ptCourse.getTrainerProfileId());

        // 커리큘럼 조회 (도메인 모델 -> View 변환)
        List<CurriculumView> curriculums = ptCurriculumRepository.findAllByPtCourseId(ptCourse.getId()).stream()
                .map(c -> new CurriculumView(c.getId(), c.getSessionNo(), c.getTitle(), c.getContent()))
                .toList();
        log.debug("[PtCourseDetail] ptCourseId={} 커리큘럼 수={}", ptCourse.getId(), curriculums.size());

        // 스케쥴 조회 (도메인 모델 -> View 변환)
        List<ScheduleView> schedules = ptCourseScheduleRepository.findAllByPtCourseId(ptCourse.getId()).stream()
                .map(s -> new ScheduleView(s.getId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime()))
                .toList();
        log.debug("[PtCourseDetail] ptCourseId={} 스케줄 수={}", ptCourse.getId(), schedules.size());

        return new PtCourseDetailView(
                ptCourse.getId(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount(),
                trainer.averageRating(),
                trainer.reviewCount(),
                ptCourse.getOrganizationId(),
                ptCourse.getTrainerProfileId(),
                trainer.trainerName(),
                trainer.profileFileId(),
                trainer.introduction(),
                trainer.certifications(),
                trainer.awards(),
                curriculums,
                schedules,
                // 미구현
                List.of()
        );
    }
}
