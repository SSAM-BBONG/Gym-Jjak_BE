package com.ssambbong.gymjjak.report.application.policy;

import com.ssambbong.gymjjak.report.application.port.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/* Comment
*   - 역할 : 신고 대상자 username을 어떻게 가져올지에 대한 application 규칙
*   TODO :
*    - null 처리, 탈퇴 유저명 처리, 숨김처리 정책이 추가 될 수 있음
* */
@Component
@RequiredArgsConstructor
public class ReportTargetOwnerPolicy {

    private final UserQueryPort userQueryPort;

    public String resolveUsername(Long targetOwnerId) {
        if (targetOwnerId == null) {
            return "알 수 없음";
        }

        return userQueryPort.findUserProfile(targetOwnerId)
                .map(UserProfileView::username)
                .orElse("알 수 없음");
    }
}
