package com.ssambbong.gymjjak.user.application.port.out;

public interface MailPort {

    String encode(String rawPassword);

    String generate();

    void sendTemporaryPassword(String email, String temporaryPassword);
}
