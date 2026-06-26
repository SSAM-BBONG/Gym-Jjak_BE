package com.ssambbong.gymjjak.user.application.event;

public record OrganizationAccountCreatedMailEvent(
        String email,
        String temporaryPassword
) {
}
