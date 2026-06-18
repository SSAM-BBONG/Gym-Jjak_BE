package com.ssambbong.gymjjak.user.adapter.in.web.request;

import com.ssambbong.gymjjak.user.application.command.IssueTemporaryPasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record IssueTemporaryPasswordRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "비밀번호 재설정 이메일", example = "user@naver.com")
        String username
) {
    public IssueTemporaryPasswordCommand toCommand() {
        return new IssueTemporaryPasswordCommand(username);
    }
}
