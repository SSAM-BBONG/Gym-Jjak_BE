package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.user.application.command.IssueTemporaryPasswordCommand;

public interface MailUseCase {
    void issueTemporaryPassword(IssueTemporaryPasswordCommand command);
}
