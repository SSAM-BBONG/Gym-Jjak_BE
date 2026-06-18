package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.CurriculumNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PtCurriculumQueryAdapter implements PtCurriculumQueryPort {

    private final PtCurriculumRepository ptCurriculumRepository;

    @Override
    public List<CurriculumSummary> findAllByPtCourseId(Long ptCourseId) {
        return ptCurriculumRepository.findAllByPtCourseId(ptCourseId)
                .stream()
                .sorted(Comparator.comparingInt(c -> c.getSessionNo()))
                .map(c -> new CurriculumSummary(
                        c.getId(),
                        c.getSessionNo(),
                        c.getTitle()
                ))
                .toList();
    }

    @Override
    public CurriculumSummary findById(Long ptCurriculumId) {
        return ptCurriculumRepository.findById(ptCurriculumId)
                .map(c -> new CurriculumSummary(c.getId(), c.getSessionNo(), c.getTitle()))
                .orElseThrow(CurriculumNotFoundException::new);
    }
}
