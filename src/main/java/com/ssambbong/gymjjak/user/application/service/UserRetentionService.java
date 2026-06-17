package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.user.application.port.in.DeleteWithdrawnUsersUsecase;
import com.ssambbong.gymjjak.user.application.port.out.DeleteWithdrawnUserPort;
import com.ssambbong.gymjjak.user.application.retention.UserRetentionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRetentionService implements DeleteWithdrawnUsersUsecase {

    private static final String JOB_NAME = "user-withdrawn-retention";

    private final DeleteWithdrawnUserPort deleteWithdrawnUserPort;
    private final UserRetentionProperties userRetentionProperties;

    @Override
    public RetentionJobResult deleteExpiredWithdrawnUsers(LocalDateTime now) {
        LocalDateTime threshold = userRetentionProperties.threshold(now);

        int candidateCount = deleteWithdrawnUserPort.countWithdrawnUsersBefore(threshold);
        if (candidateCount == 0) {
            return RetentionJobResult.empty(JOB_NAME);
        }
        int deletedParentCount = deleteWithdrawnUserPort.deleteWithdrawnUsersBefore(threshold, userRetentionProperties.batchSize());

        return new RetentionJobResult(
                JOB_NAME,
                candidateCount,
                0,
                deletedParentCount
        );
    }
}
