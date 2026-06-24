package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtReservationResponseCode implements ResponseCode {

    PT_RESERVATION_CREATED("PT_RESERVATION_201", "PT 예약 성공"),
    MY_PT_RECORDS_FETCHED("MY_PT_RECORDS_200", "내 PT 기록 목록 조회 성공"),
    MY_PT_RECORD_DETAIL_FETCHED("MY_PT_RECORD_DETAIL_200", "내 PT 기록 상세 조회 성공"),
    PT_RESERVATION_STATUS_UPDATED("PT_RESERVATION_STATUS_200", "PT 수강 상태 변경 성공"),
    PT_RESERVATION_CANCELLED("PT_RESERVATION_CANCELLED", "PT 예약이 취소되었습니다");

    private final String code;
    private final String message;
}
