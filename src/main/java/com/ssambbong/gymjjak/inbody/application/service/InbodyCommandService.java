package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyCommandUseCase;
import com.ssambbong.gymjjak.inbody.domain.exception.DuplicateInbodyMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.exception.FutureMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyErrorCode;
import com.ssambbong.gymjjak.inbody.domain.exception.InbodyRequiredFieldException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InbodyCommandService implements InbodyCommandUseCase {

    private final InbodyRepository inbodyRepository;
    private final Clock clock;

    @Monitored(
            name = "gymjjak.inbody.command.duration",
            domain = "inbody",
            action = "create"
    )
    @Override
    public CreateInbodyResult createInbody(CreateInbodyCommand command) {
        log.info(
                "event=inbody_create_started userId={}, measuredDate={}",
                command.userId(),
                command.measuredDate()
        );

        validateCreateCommand(command);

        Inbody inbody = Inbody.create(
                command.userId(),
                command.measuredDate(),
                command.height(),
                command.weight(),
                command.bodyFatPercentage(),
                command.skeletalMuscleMass()
        );

        Inbody savedInbody = inbodyRepository.save(inbody);

        log.info(
                "event=inbody_create_completed userId={}, inbodyId={}, measuredDate={}",
                savedInbody.getUserId(),
                savedInbody.getId(),
                savedInbody.getMeasuredDate()
        );

        return new CreateInbodyResult(savedInbody.getId());
    }

    private void validateCreateCommand(CreateInbodyCommand command) {
        if (command.userId() == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.USER_ID_REQUIRED);
        }
        if (command.measuredDate() == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.MEASURED_DATE_REQUIRED);
        }
        if (command.measuredDate().isAfter(LocalDate.now(clock))) {
            throw new FutureMeasuredDateException(command.measuredDate());
        }
        // 당일 중복 등록 검증
        validateDuplicateMeasuredDate(command.userId(), command.measuredDate());
    }

    // 사용자별 동일 측정일 인바디 중복 등록 검증
    private void validateDuplicateMeasuredDate(Long userId, LocalDate measuredDate) {
        if (inbodyRepository.existsByUserIdAndMeasuredDate(userId, measuredDate)) {
            throw new DuplicateInbodyMeasuredDateException(userId, measuredDate);
        }
    }

}
