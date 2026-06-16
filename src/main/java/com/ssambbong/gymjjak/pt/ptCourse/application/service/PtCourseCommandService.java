package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtCourseCommandService implements PtCourseCommandUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public Long createPtCourse(CreatePtCourseCommand command) {

        // 커리큘럼 유효성 사전 검증
        if (command.curriculums() == null || command.curriculums().isEmpty()) {
            log.warn("[PtCourseCreate] 커리큘럼 없음 - userId={}", command.userId());
            throw new PtCourseInvalidException();
        }
        int curriculumCount = command.curriculums().size();

        // 같은 요청 내 sessionNo 중복 검증
        long distinctSessionNo = command.curriculums().stream()
                .map(CreatePtCourseCommand.CurriculumData::sessionNo)
                .distinct()
                .count();
        if (distinctSessionNo != curriculumCount) {
            log.warn("[PtCourseCreate] 커리큘럼 sessionNo 중복 - userId={}", command.userId());
            throw new PtCourseInvalidException();
        }

        // 스케줄 유효성 사전 검증
        if (command.schedules() == null || command.schedules().isEmpty()) {
            log.warn("[PtCourseCreate] 스케줄 없음 - userId={}", command.userId());
            throw new PtCourseInvalidException();
        }
        int scheduleCount = command.schedules().size();

        // 같은 요청 내 (dayOfWeek, startTime, endTime) 조합 중복 검증 (파싱 후 정규화된 값 기준)
        long distinctSchedule = command.schedules().stream()
                        .map(s -> normalizeScheduleKey(s.dayOfWeek(), s.startTime(), s.endTime()))
                        .distinct()
                        .count();
        if (distinctSchedule != scheduleCount) {
            log.warn("[PtCourseCreate] 스케줄 슬롯 중복 - userId={}", command.userId());
            throw new PtCourseInvalidException();
        }

        log.debug("[PtCourseCreate] userId={}, categoryId={}, tagId={}, price={}, curriculumCount={}, scheduleCount={}",
                command.userId(), command.categoryId(), command.tagId(), command.price(), curriculumCount, scheduleCount);

        // userId로 trainerProfileId, organizationId 조회
        TrainerProfileQueryPort.TrainerInfo trainerInfo =
                trainerProfileQueryPort.findByUserId(command.userId());

        // 도메인 객체 생성 (totalSessionCount = curriculums.size())
        PtCourse ptCourse = PtCourse.create(
                trainerInfo.organizationId(),
                trainerInfo.trainerProfileId(),
                command.categoryId(),
                command.tagId(),
                command.thumbnailFileId(),
                command.title(),
                command.description(),
                command.price(),
                curriculumCount
        );

        // 저장 후 id 반환
        PtCourse saved = ptCourseRepository.save(ptCourse);

        // 커리큘럼 저장
        List<PtCurriculum> curriculums = command.curriculums().stream()
                        .map(c -> PtCurriculum.create(saved.getId(), c.sessionNo(), c.title(), c.content()))
                        .toList();
        ptCurriculumRepository.saveAll(curriculums);

        // 스케줄 저장
        List<PtCourseSchedule> schedules = command.schedules().stream()
                        .map(s -> PtCourseSchedule.create(saved.getId(), s.dayOfWeek(), s.startTime(), s.endTime()))
                        .toList();
        ptCourseScheduleRepository.saveAll(schedules);

        log.info("[PtCourseCreate] ptCourseId={}", saved.getId());
        return saved.getId();
    }

    // 스케줄 슬롯 중복 검증용 정규화 키 생성 (파싱 실패 시 도메인 예외로 변환)
    private String normalizeScheduleKey(String dayOfWeek, String startTime, String endTime) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            return day + "|" + start + "|" + end;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new PtCourseInvalidException();
        }
    }
}
