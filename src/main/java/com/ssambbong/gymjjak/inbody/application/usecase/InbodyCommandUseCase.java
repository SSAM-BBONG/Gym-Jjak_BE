package com.ssambbong.gymjjak.inbody.application.usecase;

import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.command.UpdateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;

public interface InbodyCommandUseCase {

    CreateInbodyResult createInbody(CreateInbodyCommand command);

    void updateInbody(UpdateInbodyCommand command, Long inbodyId);
}
