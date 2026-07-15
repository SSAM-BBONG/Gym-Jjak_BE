package com.ssambbong.gymjjak.inbody.application.service;

import com.ssambbong.gymjjak.global.domain.common.exception.CommonErrorCode;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.command.UpdateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyCommandUseCase;
import com.ssambbong.gymjjak.inbody.domain.exception.*;
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

    @Monitored(
            name = "gymjjak.inbody.command.duration",
            domain = "inbody",
            action = "update"
    )
    @Override
    public void updateInbody(UpdateInbodyCommand command, Long inbodyId) {
        // 수정 시작 로그
        log.info(
                "event=inbody_update_started userId={}, inbodyId={}",
                command.userId(),
                inbodyId
        );

        // command 검증
        validateUpdateCommand(command, inbodyId);

        // 본인 인바디 검증
        Inbody inbody = getOwnedInbody(inbodyId, command.userId());

        // 당일 수정 검증
        validateUpdatableToday(inbody);

        // 도메인 업데이트
        inbody.update(
                command.height(),
                command.weight(),
                command.bodyFatPercentage(),
                command.skeletalMuscleMass()
        );

        Inbody updatedInbody = inbodyRepository.save(inbody);

        log.info(
                "event=inbody_update_completed userId={}, inbodyId={}",
                updatedInbody.getUserId(),
                updatedInbody.getId()
        );
    }

    private void validateUpdateCommand(UpdateInbodyCommand command, Long inbodyId) {
        if (inbodyId == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.INBODY_ID_REQUIRED);
        }

        if (command.userId() == null) {
            throw new InbodyRequiredFieldException(InbodyErrorCode.USER_ID_REQUIRED);
        }
    }

    private Inbody getOwnedInbody(Long inbodyId, Long userId) {
        return inbodyRepository.findByIdAndUserId(inbodyId, userId)
                .orElseThrow(() -> new InbodyNotFoundException(inbodyId));
    }

    private void validateUpdatableToday(Inbody inbody) {
        LocalDate today = LocalDate.now(clock);

        if (!inbody.getCreatedAt().toLocalDate().isEqual(today)) {
            throw new InbodyUpdateNotAllowedException(inbody.getId());
        }
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
