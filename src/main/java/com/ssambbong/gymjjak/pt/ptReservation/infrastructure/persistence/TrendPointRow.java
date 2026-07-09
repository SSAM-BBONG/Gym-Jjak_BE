package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import java.time.LocalDate;

// [dashboard] 이용자 추이 집계 쿼리 projection
public interface TrendPointRow {
    LocalDate getDate();
    Long getCount();
}
