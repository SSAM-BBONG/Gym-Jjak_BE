package com.ssambbong.gymjjak.payments.subscription.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private static final int BATCH_SIZE = 500;

    private final SubscriptionExpirationBatchService batchService;
    private final Clock clock;

    // 만료 대상을 한 번에 적재하지 않고 500건씩 나누어 처리한다.
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now(clock);
        int batchCount = 0;
        int candidateCount = 0;
        int expiredCount = 0;
        int unpaidUserCount = 0;

        while (true) {
            SubscriptionExpirationBatchResult result = batchService.expireNextBatch(now, BATCH_SIZE);
            if (result.candidateCount() == 0) {
                break;
            }

            batchCount++;
            candidateCount += result.candidateCount();
            expiredCount += result.expiredCount();
            unpaidUserCount += result.unpaidUserCount();

            // 모든 후보가 건너뛰어진 경우 동일 대상을 무한 조회하지 않도록 종료한다.
            if (result.expiredCount() == 0) {
                log.warn("event=subscription_expiration_stopped reason=no_progress candidates={}",
                        result.candidateCount());
                break;
            }
        }

        log.info("event=subscription_expiration_completed batches={} candidates={} expired={} unpaidUsers={}",
                batchCount, candidateCount, expiredCount, unpaidUserCount);
    }
}
