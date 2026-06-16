package com.ssambbong.gymjjak.organization.organizationApplication.application.port;

public interface UserCreationPort {

    /**
     * 조직 신청 승인 시 ORGANIZATION 역할의 계정을 생성합니다.
     *
     * @param loginId            신청서의 requestedLoginId → users.username
     * @param representativeName 신청서의 representativeName (대표자 실명) → users.name
     * @param businessName       신청서의 businessName (시설명) → users.nickname
     * @param representativePhone 신청서의 representativePhone (대표자 번호) → users.phone
     * @return 생성된 유저의 user_id
     *
     * 구현 시 주의사항:
     * - role은 ORGANIZATION으로 고정
     * - 임시 비밀번호는 User 도메인에서 생성 및 이메일 발송 처리
     */
    Long createOrganizationAccount(
            String loginId,
            String representativeName,
            String businessName,
            String representativePhone
    );
}
