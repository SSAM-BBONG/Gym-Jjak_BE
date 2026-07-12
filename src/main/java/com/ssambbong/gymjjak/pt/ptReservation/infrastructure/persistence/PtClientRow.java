package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import java.time.LocalDateTime;

// [dashboard] 조직 PT 수강생 목록 쿼리 projection
public interface PtClientRow {
    String getUserName();
    LocalDateTime getEnrolledAt();
    Integer getProgressCount();
    Integer getTotalSessionCount();
}
