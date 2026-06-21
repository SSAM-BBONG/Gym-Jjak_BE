package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import java.util.Optional;

public interface TrainerQueryPort {

    Optional<Long> findTrainerProfileIdByUserId(Long userId);
}
