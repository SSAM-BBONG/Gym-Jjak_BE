package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtCourseQueryService implements PtCourseQueryUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final FileUseCase fileUseCase;

    // 목록 조회
    @Override
    public List<PtCourseView> findAllPtCourses() {
        log.debug("[PTCourseList] 목록 조회 시작");

        List<PtCourseView> result = ptCourseRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(ptCourse -> ptCourse.getStatus() == PtCourseStatus.VISIBLE)
                .map(this::toView)
                .toList();

        log.info("[PtCourseList] 조회된 PT 강습 수={}", result.size());
        return result;
    }

    // 상세 조회
    @Override
    public PtCourseView findPtCourseDetail(Long ptCourseId) {
        log.debug("[PtCourseDetail] ptCourseId={}", ptCourseId);

        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        // visible 확인
        if (ptCourse.getStatus() != PtCourseStatus.VISIBLE) {
            throw new PtCourseNotFoundException();
        }

        log.info("[PtCourseDetail] ptCourseId={} 조회 완료", ptCourseId);
        return toView(ptCourse);
    }

    // PtCourse -> PtCourseView 반환
    private PtCourseView toView(PtCourse ptCourse) {
        // thumbnailFileId가 있으면 Presigned URL 발급
        String thumbnailUrl = ptCourse.getThumbnailFileId() != null
                ? fileUseCase.getPresignedUrl(ptCourse.getThumbnailFileId())
                : null;

        return new PtCourseView(
                ptCourse.getId(),
                ptCourse.getCategoryId(),
                ptCourse.getTagId(),
                thumbnailUrl,
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount(),
                ptCourse.getStatus()
        );
    }
}
