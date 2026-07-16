package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PtCoursePaymentQueryAdapter implements PtCoursePaymentQueryPort {

    private final SpringDataPtCourseRepository springDataPtCourseRepository;

    @Override
    public Optional<PtCoursePaymentInfo> findPtCoursePaymentInfo(Long ptCourseId) {
        return springDataPtCourseRepository.findByIdAndDeletedAtIsNull(ptCourseId)
                .map(entity -> new PtCoursePaymentInfo(entity.getTitle(), entity.getPrice()));
    }
}
