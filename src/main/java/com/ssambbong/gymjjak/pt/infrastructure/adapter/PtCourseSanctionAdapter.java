package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.report.application.port.PtCourseSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtCourseSanctionAdapter implements PtCourseSanctionPort {

    private final PtCourseRepository ptCourseRepository;

    @Override
    @Transactional
    public void applySanction(Long ptCourseId, ReportSanctionAction action) {
        log.debug("[PtCourseBlind] ptCourseId={}, action={}", ptCourseId, action);

        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        switch (action) {
            case APPLY_AUTO_BLIND -> ptCourse.blind();
            case RELEASE_AUTO_BLIND -> ptCourse.unblind();
            // TODO : 현지야, 이거 수동 제재 (관리자 최종 블라인드는 delete로 상태값 변경 맞지?) 이거 확인해줘.
            //  어차피 블라인드 될 때, 이미 soft delete로 삭제일 추가 시키잖아 맞지? 상태값만 변경하면 안된데이!
            //  일단 delete까지 domain 클래스에 추가만 해놨어
            case APPLY_MANUAL_BLIND -> ptCourse.delete();
        }

        ptCourseRepository.save(ptCourse);

        log.info("[PtCourseBlind] ptCourseId={}, action={}, status={}",
                ptCourseId, action, ptCourse.getStatus());
    }
}
