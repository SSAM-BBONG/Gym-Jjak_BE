package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.exception.OrganizationTrainerAlreadyExistsException;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationTrainerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * 트레이너 신청서 승인 시 조직트레이너 테이블에 저장 요청해주는 port
 */
@Component
@RequiredArgsConstructor
public class TrainerApplicationOrganizationTrainerAdapter
        implements TrainerApplicationOrganizationTrainerPort {

    private final OrganizationTrainerRepository organizationTrainerRepository;

    @Override
    public void registerApprovedTrainer(
            Long organizationId,
            Long trainerProfileId,
            Long registeredBy
    ) {
        // 승인된 트레이너 중복 등록 방지 기능
        if (organizationTrainerRepository.existsActiveByOrganizationIdAndTrainerProfileId(
                organizationId,
                trainerProfileId
        )) {
            throw new OrganizationTrainerAlreadyExistsException();
        }

        // 조직 트레이너 도메인 생성 기능
        OrganizationTrainer organizationTrainer =
                OrganizationTrainer.create(
                        organizationId,
                        trainerProfileId,
                        registeredBy
                );

        // 조직 트레이너 저장 기능
        try {
            organizationTrainerRepository.save(organizationTrainer);
        } catch (DataIntegrityViolationException exception) {
            if (isActiveOrganizationTrainerConstraint(exception)) {
                throw new OrganizationTrainerAlreadyExistsException(exception);
            }

            throw exception;
        }
    }

    // 활성 조직 트레이너 중복 제약조건 확인 기능
    private boolean isActiveOrganizationTrainerConstraint(
            DataIntegrityViolationException exception
    ) {
        // DB가 보내준 가장 구체적인 에러 꺼내기
        String message = exception.getMostSpecificCause().getMessage();

        // 유니크 조건 있는지 확인, 맞다면 더 확실한 DB 무결성 에러를 return함
        return message != null
                && message.contains("uk_active_organization_trainer");
    }
}
