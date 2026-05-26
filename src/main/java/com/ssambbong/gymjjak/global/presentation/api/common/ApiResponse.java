package com.ssambbong.gymjjak.global.presentation.api.common;

import org.springframework.http.HttpStatus;

/* Comment
    이거 설명 이해 ㄱㄴ?
*   - 1번, 매개변수에 ResponseCode, data 받는 ok(), created() 는
*   각 도메인에서 정의한 응답 enum을 넣고 호출하는 메서드
*   - 2번, 매개변수에 code, msg, data 를 받는 ok(), created() 는
*   위 1번 메서드는 2번 메서드를 호출하고 아래 3번을 호출한다.
*   - 3번, of() 메서드는 실제 json 응답을 조립해주는 메서드
* */
public record ApiResponse<T>(
        int status,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String code, String message, T data) {
        return new ApiResponse<>(httpStatus.value(), code, message, data);
    }

    public static ApiResponse<Void> of(HttpStatus httpStatus, String code, String message) {
        return new ApiResponse<>(httpStatus.value(), code, message, null);
    }

    // 200 ok - body 값 있을 때
    public static <T> ApiResponse<T> ok(String code, String message, T data) {
        return of(HttpStatus.OK, code, message, data);
    }

    // 200 ok - body 값 없을 때
    public static ApiResponse<Void> ok(String code, String message) {
        return of(HttpStatus.OK, code, message);
    }

    // 200 ok - responseCode는 각 도메인에서 enum으로 만들기
    public static <T> ApiResponse<T> ok(ResponseCode responseCode, T data) {
        return ok(responseCode.code(), responseCode.message(), data);
    }

    public static ApiResponse<Void> ok(ResponseCode responseCode) {
        return ok(responseCode.code(), responseCode.message());
    }

    // 201 created - body 값 있음
    public static <T> ApiResponse<T> created(String code, String message, T data) {
        return of(HttpStatus.CREATED, code, message, data);
    }
    // 201 created - body 값 없음
    public static ApiResponse<Void> created(String code, String message) {
        return of(HttpStatus.CREATED, code, message);
    }
    // 201 created - body 값 있음
    public static <T> ApiResponse<T> created(ResponseCode responseCode, T data) {
        return created(responseCode.code(), responseCode.message(), data);
    }
    // 201 created - body 값 없음
    public static ApiResponse<Void> created(ResponseCode responseCode) {
        return created(responseCode.code(), responseCode.message());
    }
}
