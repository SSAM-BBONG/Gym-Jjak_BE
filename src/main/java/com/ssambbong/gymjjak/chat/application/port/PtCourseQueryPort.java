package com.ssambbong.gymjjak.chat.application.port;

import java.util.Optional;

public interface PtCourseQueryPort {
    Optional<Long> findTrainerProfileIdByCourseId(Long ptCourseId);
}
