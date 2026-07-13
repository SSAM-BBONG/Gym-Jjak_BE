package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import com.ssambbong.gymjjak.inbody.domain.exception.DuplicateInbodyMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InbodyRepositoryAdapter implements InbodyRepository {

    private final SpringDataInbodyRepository springDataInbodyRepository;
    private final InbodyPersistenceMapper inbodyPersistenceMapper;

    @Override
    public Inbody save(Inbody inbody) {
        try {
            InbodyJpaEntity savedEntity = springDataInbodyRepository.save(
                    inbodyPersistenceMapper.toEntity(inbody)
            );

            return inbodyPersistenceMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateMeasuredDateConstraint(exception)) {
                // 같은 사용자, 측정일 기록 존재 시 예외
                throw new DuplicateInbodyMeasuredDateException(
                        inbody.getUserId(),
                        inbody.getMeasuredDate()
                );
            }

            throw exception;
        }
    }

    @Override
    public boolean existsByUserIdAndMeasuredDate(Long userId, LocalDate measuredDate) {
        return springDataInbodyRepository.existsByUserIdAndMeasuredDate(userId, measuredDate);
    }

    // 사용자별 측정일 유니크 제약 위반 여부 확인
    private boolean isDuplicateMeasuredDateConstraint(DataIntegrityViolationException exception) {
        return Optional.ofNullable(exception.getMostSpecificCause())
                .map(Throwable::getMessage)
                .map(message -> message.contains("uk_inbody_user_measured_date"))
                .orElse(false);
    }

}
