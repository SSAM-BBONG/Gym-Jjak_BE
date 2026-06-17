package com.ssambbong.gymjjak.user.adapter.in.web.request;

import com.ssambbong.gymjjak.user.application.command.IssueTemporaryPasswordCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record IssueTemporaryPasswordRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String username
) {
    public IssueTemporaryPasswordCommand toCommand() {
        return new IssueTemporaryPasswordCommand(username);
    }
}
