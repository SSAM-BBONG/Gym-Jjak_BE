package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.IssueTemporaryPasswordCommand;
import com.ssambbong.gymjjak.user.application.port.in.MailUseCase;
import com.ssambbong.gymjjak.user.application.port.out.MailPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService implements MailUseCase {

    private final MailPort mailPort;
    private final UserPort userPort;

    @Override
    public void issueTemporaryPassword(IssueTemporaryPasswordCommand command) {

        log.debug("event=email_send_start username={}", command.username());

        User user = userPort.findByUsername(command.username())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        String temporaryPassword = mailPort.generate();

        String encodedPassword = mailPort.encode(temporaryPassword);

        user.changePassword(encodedPassword, LocalDateTime.now());

        userPort.save(user);

        mailPort.sendTemporaryPassword(
                user.getUsername(),
                temporaryPassword
        );

        log.info("event=email_send_succeed username={}", command.username());
    }
}
