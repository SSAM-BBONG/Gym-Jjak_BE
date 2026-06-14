package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.pt.application.port.PtCourseEnrichQueryPort;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
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

    private Map<Long, String> buildCategoryMap() {
        return categoryQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        CategoryQueryUseCase.CategoryView::categoryId,
                        CategoryQueryUseCase.CategoryView::name
                ));
    }

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

    private PtCourseDetailView toDetailView(PtCourse ptCourse) {
        PtCourseEnrichQueryPort.TrainerDisplayInfo trainer =
                enrichQueryPort.findTrainerProfileById(ptCourse.getTrainerProfileId());

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
                List.of(),
                List.of(),
                List.of()
        );
    }
}
