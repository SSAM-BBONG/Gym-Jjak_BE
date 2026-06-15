package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtReservationResponseCode implements ResponseCode {

    PT_RESERVATION_CREATED("PT_RESERVATION_201", "PT 예약 성공"),
    MY_PT_RECORDS_FETCHED("MY_PT_RECORDS_200", "내 PT 기록 목록 조회 성공");

    private final String code;
    private final String message;
}
