package com.ssambbong.gymjjak.organization.organizationApplication.application.port;

public interface UserLoginIdValidationPort {

    /**
     * 조직 신청 시 요청 로그인 ID의 유효성을 검증합니다.
     * - 이메일 형식 검증
     * - users 테이블 기준 중복 검증
     *
     * @throws com.ssambbong.gymjjak.organization.organizationApplication.exception.InvalidLoginIdFormatException 이메일 형식이 아닌 경우
     * @throws com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException users 테이블에 이미 존재하는 경우
     */
    void validate(String loginId);
}
