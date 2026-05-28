package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;

public interface UserCommandUseCase {

    void registerUser(RegisterUserCommand command);
}
