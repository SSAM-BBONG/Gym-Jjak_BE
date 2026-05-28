package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PtCourseCommandServiceTest {

    @Mock
    private PtCourseRepository ptCourseRepository;

    // 테스트할 식제 객체를 생성하고 위에서 만든 @Mock 객체들을 자동으로 주입해줌
    @InjectMocks
    private PtCourseCommandService ptCourseCommandService;

    @Test
    @DisplayName("정상적인 요청으로 PT 강습 등록 시 ptCourseId가 반환되어야 한다")
    void createPtCourse_success() {

        // given : 테스트에 필요한 데이터 준비
        // 실제 Http 요청 대신 Command 객체를 직접 만들어 테스트
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L,                                    // organizationId (임시)
                1L,                                    // trainerProfileId (임시)
                1L,                                    // categoryId
                1L,                                    // tagId
                null,                                  // thumbnailFileId (선택)
                "체계적인 가슴 집중 PT",                // title
                "가슴 근육 발달에 특화된 12주 프로그램", // description
                50000,                                 // price
                12                                     // totalSessionCount
        );

        // DB에 저장됐다고 가정한 PtCourse 객체
        PtCourse savedPtCourse = PtCourse.restore(
                1L, 1L, 1L, 1L, 1L, null,
                "체계적인 가슴 집중 PT",
                "가슴 근육 발달에 특화된 12주 프로그램",
                50000, 12, false, false, PtCourseStatus.VISIBLE
        );

        // Mock 동작 정의: ptCourseRepository.save()가 호출되면 savedPtCourse를 반환하도록 설정
        // any(PtCourse.class): 어떤 PtCourse 객체가 들어오든 상관없이 savedPtCourse 반환
        when(ptCourseRepository.save(any(PtCourse.class))).thenReturn(savedPtCourse);

        // when : 실제 테스트할 메서드 실행
        Long ptCourseId = ptCourseCommandService.createPtCourse(command);

        // then : 결과 검증
        assertEquals(1L, ptCourseId);

        // ptCourseRepository.save()가 정확히 1번 호출됐는지 확인
        verify(ptCourseRepository).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("title이 비어있으면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_emptyTitle_throwsException() {

        // given : title이 빈 문자열인 잘못된 요청
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, null,
                "",
                "설명", 5000, 12
        );
        // when & then : 예외 발생 확인
        // assertThrows: 람다 안에서 특정 예외가 발생하는지 검증
        // PtCourse.create() 내부에서 검증하기 때문에 repository.save()는 호출되지 않음
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        // save가 호출되지 않았는지 확인
        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("price가 음수이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_negativePrice_throwsException() {

        // Given: price가 음수인 잘못된 요청
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, null,
                "PT 강습 제목", "설명",
                -1,  // 음수 price → 도메인 규칙 위반
                12
        );
        // When & Then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }

    @Test
    @DisplayName("totalSessionCount가 1 미만이면 PtCourseInvalidException이 발생해야 한다")
    void createPtCourse_zeroTotalSessionCount_throwsException() {

        // Given: totalSessionCount가 0인 잘못된 요청
        CreatePtCourseCommand command = new CreatePtCourseCommand(
                1L, 1L, 1L, 1L, null,
                "PT 강습 제목", "설명",
                50000,
                0  // 0회차 → 도메인 규칙 위반 (최소 1회 이상)
        );

        // When & Then
        assertThrows(PtCourseInvalidException.class,
                () -> ptCourseCommandService.createPtCourse(command));

        verify(ptCourseRepository, never()).save(any(PtCourse.class));
    }

}