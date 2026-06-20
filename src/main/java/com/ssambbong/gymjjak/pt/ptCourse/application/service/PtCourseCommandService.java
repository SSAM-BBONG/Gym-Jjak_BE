package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.ChangePtCourseStatusCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseForbiddenException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
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
    private final FileUseCase fileUseCase;

    @Override
    public Long createPtCourse(CreatePtCourseCommand command) {

        int curriculumCount = command.curriculums() == null ? 0 : command.curriculums().size();

        if (curriculumCount == 0) {
            log.warn(
                    "event=pt_course_create_failed, reason=no_curriculum, userId={}",
                    command.userId()
            );
            throw new PtCourseInvalidException();
        }

        long distinctSessionNo = command.curriculums().stream()
                .map(CreatePtCourseCommand.CurriculumData::sessionNo)
                .distinct()
                .count();
        if (distinctSessionNo != curriculumCount) {
            log.warn(
                    "event=pt_course_create_failed, reason=duplicate_session_no, userId={}",
                    command.userId()
            );
            throw new PtCourseInvalidException();
        }

        int scheduleCount = command.schedules() == null ? 0 : command.schedules().size();

        if (scheduleCount == 0) {
            log.warn(
                    "event=pt_course_create_failed, reason=no_schedule, userId={}",
                    command.userId()
            );
            throw new PtCourseInvalidException();
        }

        long distinctSchedule = command.schedules().stream()
                .map(s -> normalizeScheduleKey(s.dayOfWeek(), s.startTime(), s.endTime()))
                .distinct()
                .count();
        if (distinctSchedule != scheduleCount) {
            log.warn(
                    "event=pt_course_create_failed, reason=duplicate_schedule, userId={}",
                    command.userId()
            );
            throw new PtCourseInvalidException();
        }

        log.info(
                "event=pt_course_create_started, userId={}, categoryId={}, tagId={}, price={}, curriculumCount={}, scheduleCount={}",
                command.userId(), command.categoryId(), command.tagId(), command.price(),
                curriculumCount, scheduleCount
        );

        TrainerProfileQueryPort.TrainerInfo trainerInfo =
                trainerProfileQueryPort.findByUserId(command.userId());

        Long thumbnailFileId = registerThumbnailFile(command.userId(), command.thumbnailFile());

        PtCourse ptCourse = PtCourse.create(
                trainerInfo.organizationId(),
                trainerInfo.trainerProfileId(),
                command.categoryId(),
                command.tagId(),
                thumbnailFileId,
                command.title(),
                command.description(),
                command.price(),
                curriculumCount
        );

        PtCourse saved = ptCourseRepository.save(ptCourse);

        List<PtCurriculum> curriculums = command.curriculums().stream()
                .map(c -> PtCurriculum.create(saved.getId(), c.sessionNo(), c.title(), c.content()))
                .toList();
        ptCurriculumRepository.saveAll(curriculums);

        List<PtCourseSchedule> schedules = command.schedules().stream()
                .map(s -> PtCourseSchedule.create(saved.getId(), s.dayOfWeek(), s.startTime(), s.endTime()))
                .toList();
        ptCourseScheduleRepository.saveAll(schedules);

        log.info(
                "event=pt_course_create_succeeded, ptCourseId={}",
                saved.getId()
        );
        return saved.getId();
    }

    // PT 상태 변경
    @Override
    public void changePtCourseStatus(ChangePtCourseStatusCommand command) {
        log.debug("event=pt_course_status_change_started, userId={}, ptCourseId={}, status={}",
                command.userId(), command.ptCourseId(), command.status());

        // PT 존재 여부 검증
        PtCourse ptCourse = ptCourseRepository.findById(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("event=pt_course_status_change_failed, reason=not_found, ptCourseId={}",
                            command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

        // 본인 PT인지 여부 검증
        TrainerProfileQueryPort.TrainerInfo trainerInfo =
                trainerProfileQueryPort.findByUserId(command.userId());
        if (!ptCourse.getTrainerProfileId().equals(trainerInfo.trainerProfileId())) {
            log.warn("event=pt_course_status_change_failed, reason=forbidden, userId={}, ptCourseId={}",
                    command.userId(), command.ptCourseId());
            throw new PtCourseForbiddenException();
        }

        ptCourse.changeStatus(command.status());
        ptCourseRepository.update(ptCourse);
        log.info("event=pt_course_status_change_succeeded, ptCourseId={}, status={}",
                command.ptCourseId(), command.status());
    }

    // 썸네일 파일 등록. thumbnailFile이 null이면 null 반환.
    private Long registerThumbnailFile(Long userId, UploadedFileMetadataCommand thumbnailFile) {
        if (thumbnailFile == null) return null;

        List<FileRegistrationResult> results = fileUseCase.registerFiles(List.of(
                new CreateFileCommand(
                        userId,
                        thumbnailFile.fileKey(),
                        thumbnailFile.originalName(),
                        thumbnailFile.contentType(),
                        thumbnailFile.fileSize(),
                        FileType.PT_THUMBNAIL
                )
        ));

        if (results.size() != 1 || results.get(0).fileId() == null) {
            log.error(
                    "event=pt_course_thumbnail_register_failed, reason=unexpected_result, userId={}, resultSize={}",
                    userId, results.size()
            );
            throw new IllegalStateException("썸네일 파일 등록 결과가 존재하지 않습니다.");
        }

        return results.get(0).fileId();
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
