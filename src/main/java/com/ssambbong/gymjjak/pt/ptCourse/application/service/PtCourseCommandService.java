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
            throw new PtCourseInvalidException();
        }
        int curriculumCount = command.curriculums().size();

        // 같은 요청 내 sessionNo 중복 검증
        long distinctSessionNo = command.curriculums().stream()
                .map(CreatePtCourseCommand.CurriculumData::sessionNo)
                .distinct()
                .count();
        if (distinctSessionNo != curriculumCount) {
            throw new PtCourseInvalidException();
        }

        // 스케줄 유효성 사전 검증
        if (command.schedules() == null || command.schedules().isEmpty()) {
            throw new PtCourseInvalidException();
        }
        int scheduleCount = command.schedules().size();

        // 같은 요청 내 (dayOfWeek, startTime, endTime) 조합 중복 검증
        long distinctSchedule = command.schedules().stream()
                        .map(s -> s.dayOfWeek() + "|" + s.startTime() + "|" + s.endTime())
                        .distinct()
                        .count();
        if (distinctSchedule != scheduleCount) {
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
}
