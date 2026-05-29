package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;

public interface TokenCommandUsecase {

    String reissueAccessToken(ReissueTokenCommand command);
}
