package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import com.ssambbong.gymjjak.inbody.domain.exception.DuplicateInbodyMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodyRepository;
import com.ssambbong.gymjjak.inbody.domain.repository.InbodySlice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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

    @Override
    public InbodySlice findInbodySlice(
            Long userId,
            LocalDate measuredDate,
            Long inbodyId,
            int size
    ) {
        List<InbodyJpaEntity> entities = springDataInbodyRepository.findInbodySlice(
                userId,
                measuredDate,
                inbodyId,
                // 화면용 2개 + 다음 페이지 존재 확인용 1개 총 3개 조회
                PageRequest.of(0, size + 1)
        );

        boolean hasNext = entities.size() > size;

        List<Inbody> inbodies = entities.stream()
                .limit(size)
                .map(inbodyPersistenceMapper::toDomain)
                .toList();

        return new InbodySlice(inbodies, hasNext);
    }

    @Override
    public Optional<Inbody> findByIdAndUserId(Long inbodyId, Long userId) {
        return springDataInbodyRepository.findByIdAndUserId(inbodyId, userId)
                .map(inbodyPersistenceMapper::toDomain);
    }

    // 사용자별 측정일 유니크 제약 위반 여부 확인
    private boolean isDuplicateMeasuredDateConstraint(DataIntegrityViolationException exception) {
        return Optional.ofNullable(exception.getMostSpecificCause())
                .map(Throwable::getMessage)
                .map(message -> message.contains("uk_inbody_user_measured_date"))
                .orElse(false);
    }

}
