package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import com.ssambbong.gymjjak.inbody.domain.exception.DuplicateInbodyMeasuredDateException;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InbodyRepositoryAdapterTest {

    private SpringDataInbodyRepository springDataInbodyRepository;
    private InbodyPersistenceMapper inbodyPersistenceMapper;
    private InbodyRepositoryAdapter inbodyRepositoryAdapter;

    @BeforeEach
    void setUp() {
        springDataInbodyRepository = mock(SpringDataInbodyRepository.class);
        inbodyPersistenceMapper = mock(InbodyPersistenceMapper.class);
        inbodyRepositoryAdapter = new InbodyRepositoryAdapter(
                springDataInbodyRepository,
                inbodyPersistenceMapper
        );
    }

    @Test
    @DisplayName("측정일 유니크 제약 위반은 중복 인바디 예외로 변환한다")
    void save_fail_duplicateMeasuredDateConstraint() {
        Inbody inbody = createInbody();
        InbodyJpaEntity entity = mock(InbodyJpaEntity.class);
        when(inbodyPersistenceMapper.toEntity(inbody)).thenReturn(entity);
        when(springDataInbodyRepository.save(entity)).thenThrow(
                dataIntegrityViolation("Duplicate entry '1-2026-07-13' for key 'uk_inbody_user_measured_date'")
        );

        assertThatThrownBy(() -> inbodyRepositoryAdapter.save(inbody))
                .isInstanceOf(DuplicateInbodyMeasuredDateException.class);
    }

    @Test
    @DisplayName("측정일 유니크 제약이 아닌 DB 예외는 원본 예외를 전달한다")
    void save_fail_otherDataIntegrityViolation() {
        Inbody inbody = createInbody();
        InbodyJpaEntity entity = mock(InbodyJpaEntity.class);
        DataIntegrityViolationException exception = dataIntegrityViolation("Data truncation: Out of range value");
        when(inbodyPersistenceMapper.toEntity(inbody)).thenReturn(entity);
        when(springDataInbodyRepository.save(entity)).thenThrow(exception);

        assertThatThrownBy(() -> inbodyRepositoryAdapter.save(inbody))
                .isSameAs(exception);
    }

    private Inbody createInbody() {
        return Inbody.create(
                1L,
                LocalDate.of(2026, 7, 13),
                new BigDecimal("170.00"),
                new BigDecimal("70.00"),
                new BigDecimal("15.50"),
                new BigDecimal("30.20")
        );
    }

    private DataIntegrityViolationException dataIntegrityViolation(String message) {
        return new DataIntegrityViolationException(message, new SQLException(message));
    }
}
