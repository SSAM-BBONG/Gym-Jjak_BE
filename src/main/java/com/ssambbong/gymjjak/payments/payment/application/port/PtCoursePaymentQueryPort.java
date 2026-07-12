package com.ssambbong.gymjjak.payments.payment.application.port;

public interface PtCoursePaymentQueryPort {

    PtCoursePaymentInfo findPtCoursePaymentInfo(Long ptCourseId);

    record PtCoursePaymentInfo(String title, int price) {}
}
