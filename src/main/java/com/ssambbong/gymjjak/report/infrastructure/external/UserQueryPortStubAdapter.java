package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.UserQueryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

// TODO : 주원이가 Adapter 만들면, 삭제할 임시 클래스
@Component
public class UserQueryPortStubAdapter implements UserQueryPort {


    @Override
    public Optional<UserProfileView> findUserProfile(Long userId) {
        return Optional.empty();
    }
}
