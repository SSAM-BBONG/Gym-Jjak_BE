package com.ssambbong.gymjjak.pt.ptCourse.application.port;

public interface PaymentQueryPort {
    boolean existsPaidByUserIdAndPtCourseId(Long userId, Long ptCourseId);
}
