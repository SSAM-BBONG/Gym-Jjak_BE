package com.ssambbong.gymjjak.pt.ptReservation.application.port;

public interface PaymentQueryPort {

    boolean existsPaidByUserIdAndPtCourseId(Long userId, Long ptCourseId);
}
