package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.actuator;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "ptreservation")
@RequiredArgsConstructor
public class PtReservationMetricEndpoint {

    private final SpringDataPtReservationRepository ptReservationRepository;

    @ReadOperation
    public PtReservationSummary summary() {
        long total = ptReservationRepository.count();
        long completed = ptReservationRepository.countByStatus(PtReservationStatus.COMPLETED);
        long cancelled = ptReservationRepository.countByCancelledAtIsNotNull();

        double completionRate = total == 0 ? 0.0 : round1((double) completed / total * 100);
        double cancellationRate = total == 0 ? 0.0 : round1((double) cancelled / total * 100);

        return new PtReservationSummary(total, completed, cancelled, completionRate, cancellationRate);
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }

    public record PtReservationSummary(
            long totalCount,
            long completedCount,
            long cancelledCount,
            double completionRate,   // 수강 완료율 (%)
            double cancellationRate  // 취소율 (%)
    ) {}
}
