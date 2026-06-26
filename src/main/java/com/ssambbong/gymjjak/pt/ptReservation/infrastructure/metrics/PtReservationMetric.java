package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.metrics;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PtReservationMetric {

    public PtReservationMetric(MeterRegistry meterRegistry,
                               SpringDataPtReservationRepository ptReservationRepository) {
        Gauge.builder("gymjjak.pt.reservation.completed",
                        ptReservationRepository,
                        repo -> repo.countByStatus(PtReservationStatus.COMPLETED))
                .description("수강 완료된 예약 수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.pt.reservation.cancelled",
                        ptReservationRepository,
                        repo -> repo.countByCancelledAtIsNotNull())
                .description("취소된 예약 수")
                .register(meterRegistry);
    }
}
