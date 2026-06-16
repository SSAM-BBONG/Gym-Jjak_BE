package com.ssambbong.gymjjak.user.application.port.out;

import java.time.LocalDateTime;

public interface DeleteWithdrawnUserPort {

    int countWithdrawnUsersBefore(LocalDateTime threshold);

    int deleteWithdrawnUsersBefore(LocalDateTime threshold, int batchSize);
}
