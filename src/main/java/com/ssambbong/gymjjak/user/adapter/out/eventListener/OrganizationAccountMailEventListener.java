package com.ssambbong.gymjjak.user.adapter.out.eventListener;

import com.ssambbong.gymjjak.user.application.event.OrganizationAccountCreatedMailEvent;
import com.ssambbong.gymjjak.user.application.port.out.MailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationAccountMailEventListener {

    private final MailPort mailPort;

    @Async("mailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrganizationTemporaryPassword(
            OrganizationAccountCreatedMailEvent event
    ) {
        try {
            mailPort.sendTemporaryPassword(
                    event.email(),
                    event.temporaryPassword()
            );
        } catch (Exception e) {
            log.error(
                    "event=TemporaryPassword_send_failed, email={}",
                    event.email()
            );
        }
    }
}
