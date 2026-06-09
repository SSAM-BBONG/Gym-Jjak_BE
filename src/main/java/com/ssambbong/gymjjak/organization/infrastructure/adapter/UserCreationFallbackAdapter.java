package com.ssambbong.gymjjak.organization.infrastructure.adapter;

import com.ssambbong.gymjjak.organization.application.port.UserCreationPort;
import org.springframework.stereotype.Component;

/**
 * UserCreationPort 임시 구현체
 * User 도메인 담당자가 구현 완료 후 이 파일 삭제 예정
 */
@Component
public class UserCreationFallbackAdapter implements UserCreationPort {

    @Override
    public Long createOrganizationAccount(String loginId, String representativeName, String businessName, String representativePhone) {
        throw new UnsupportedOperationException("UserCreationPort 미구현 - User 도메인 담당자에게 구현 요청 필요");
    }
}
