package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.chat.application.port.PtCourseQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatPtCourseQueryAdapter implements PtCourseQueryPort {

    private final PtCourseRepository ptCourseRepository;

    @Override
    public Optional<Long> findTrainerProfileIdByCourseId(Long ptCourseId) {
        return ptCourseRepository.findById(ptCourseId)
                .map(ptCourse -> ptCourse.getTrainerProfileId());
    }
}
