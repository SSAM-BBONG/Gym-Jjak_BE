package com.ssambbong.gymjjak.payments.payment.application.port;

import java.util.Optional;

public interface PtCoursePaymentQueryPort {

    Optional<PtCoursePaymentInfo> findPtCoursePaymentInfo(Long ptCourseId);

    record PtCoursePaymentInfo(String title, int price) {}
}
