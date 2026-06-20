package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.user.application.command.*;
import com.ssambbong.gymjjak.user.application.result.*;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;

import java.util.List;

public interface UserCommandUseCase {

    void registerUser(RegisterUserCommand command);


    LoginResult login(LoginCommand loginCommand);

    void logout(LogoutCommand command);

    void verifyPassword(Long userId, String rawPassword);

    UserProfileResult findMyProfileInfo(Long userId);

    void updateProfile(UpdateProfileCommand command);

    void withdrawUser(Long userId);

    void updateUserStatus(UpdateUserStatusCommand command);

    void updatePassword(UpdatePasswordCommand command);

    PageResult<FindUserResult> findUsers(String name, int page, int size);

    PageResult<FindBlacklistUserResult> findBlacklistUsers(String name, int page, int size);

    void completeSocialSignup(CompleteSocialSignupCommand command);


}
