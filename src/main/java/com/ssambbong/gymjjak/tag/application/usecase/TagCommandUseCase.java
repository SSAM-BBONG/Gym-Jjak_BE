package com.ssambbong.gymjjak.tag.application.usecase;

import com.ssambbong.gymjjak.tag.application.command.CreateTagCommand;
import com.ssambbong.gymjjak.tag.application.command.DeleteTagCommand;
import com.ssambbong.gymjjak.tag.application.command.UpdateTagCommand;

public interface TagCommandUseCase {

    Long handle(CreateTagCommand command);
    void handle(UpdateTagCommand command);
    void handle(DeleteTagCommand command);
}
