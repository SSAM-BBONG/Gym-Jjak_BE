package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.LogoutCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;
import com.ssambbong.gymjjak.user.application.result.LoginResult;

public interface UserCommandUseCase {

    void registerUser(RegisterUserCommand command);


    LoginResult login(LoginCommand loginCommand);

    void logout(LogoutCommand command);

}
