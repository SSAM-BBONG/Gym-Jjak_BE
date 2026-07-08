package com.ssambbong.gymjjak.part.application.usecase;

import com.ssambbong.gymjjak.part.application.command.CreatePartCommand;
import com.ssambbong.gymjjak.part.application.command.DeletePartCommand;
import com.ssambbong.gymjjak.part.application.command.UpdatePartCommand;

public interface PartCommandUseCase {

    Long handle(CreatePartCommand command);
    void handle(UpdatePartCommand command);
    void handle(DeletePartCommand command);
}
