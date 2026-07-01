package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.ChangePtCourseStatusCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.DeletePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UpdatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.ptCourse.domain.event.PtCourseListCacheEvictEvent;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtReservationCountQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.CurriculumUpdateNotAllowedException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseCannotDeleteException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseForbiddenException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseHasActiveReservationException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseRequestInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final OrganizationQueryPort organizationQueryPort;
    private final PtReservationCountQueryPort ptReservationCountQueryPort;
    private final FileUseCase fileUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long createPtCourse(CreatePtCourseCommand command) {

        int curriculumCount = command.curriculums() == null ? 0 : command.curriculums().size();

        if (curriculumCount == 0) {
            log.warn(
                    "event=pt_course_create_failed, reason=no_curriculum, userId={}",
                    command.userId()
            );
            throw new PtCourseRequestInvalidException();
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
            throw new PtCourseRequestInvalidException();
        }

        int scheduleCount = command.schedules() == null ? 0 : command.schedules().size();

        if (scheduleCount == 0) {
            log.warn(
                    "event=pt_course_create_failed, reason=no_schedule, userId={}",
                    command.userId()
            );
            throw new PtCourseRequestInvalidException();
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
            throw new PtCourseRequestInvalidException();
        }

        log.info(
                "event=pt_course_create_started, userId={}, categoryId={}, tagId={}, price={}, curriculumCount={}, scheduleCount={}",
                command.userId(), command.categoryId(), command.tagId(), command.price(),
                curriculumCount, scheduleCount
        );

        Long trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(command.userId());
        Long organizationId = organizationQueryPort.findOrganizationIdByTrainerProfileId(trainerProfileId);

        Long thumbnailFileId = registerThumbnailFile(command.userId(), command.thumbnailFile());

        PtCourse ptCourse = PtCourse.create(
                organizationId,
                trainerProfileId,
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
        eventPublisher.publishEvent(new PtCourseListCacheEvictEvent());
        return saved.getId();
    }

    // PT 강습 수정
    @Override
    public Long updatePtCourse(UpdatePtCourseCommand command) {
        log.debug("event=pt_course_update_started userId={}, ptCourseId={}", command.userId(), command.ptCourseId());

        // 강습 존재 여부 확인 — 커리큘럼 수정 경합 방지를 위해 비관적 잠금 조회
        PtCourse ptCourse = ptCourseRepository.findByIdForUpdate(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("event=pt_course_update_failed reason=not_found ptCourseId={}", command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

        // 본인 강습 여부 확인
        Long trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(command.userId());
        if (!ptCourse.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_course_update_failed reason=forbidden userId={}, ptCourseId={}", command.userId(), command.ptCourseId());
            throw new PtCourseForbiddenException();
        }

        // 커리큘럼 변경 시 활성 수강생 0명 확인 (빈 리스트 = 전체 삭제도 변경에 해당)
        if (command.curriculums() != null) {
            int activeCount = ptReservationCountQueryPort
                    .countActiveByPtCourseIds(List.of(command.ptCourseId()))
                    .getOrDefault(command.ptCourseId(), 0);
            if (activeCount > 0) {
                log.warn("event=pt_course_update_failed reason=curriculum_update_not_allowed ptCourseId={}, activeCount={}", command.ptCourseId(), activeCount);
                throw new CurriculumUpdateNotAllowedException();
            }
        }

        // 썸네일 파일 등록 (null이면 기존 유지)
        Long thumbnailFileId = command.thumbnailFile() != null
                ? registerThumbnailFile(command.userId(), command.thumbnailFile())
                : null;

        // 강습 필드 수정
        ptCourse.update(
                command.categoryId(),
                command.tagId(),
                thumbnailFileId,
                command.title(),
                command.description(),
                command.price(),
                command.curriculums() != null ? command.curriculums().size() : ptCourse.getTotalSessionCount()
        );
        ptCourseRepository.update(ptCourse);

        // 커리큘럼 upsert
        if (command.curriculums() != null) {
            // sessionNo 중복 검증 (생성과 동일 규칙)
            long distinctSessionNo = command.curriculums().stream()
                    .map(UpdatePtCourseCommand.CurriculumData::sessionNo).distinct().count();
            if (distinctSessionNo != command.curriculums().size()) {
                log.warn("event=pt_course_update_failed reason=duplicate_session_no ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            // DB에 있는 기존 커리큘럼 ID 목록
            List<Long> existingIds = ptCurriculumRepository.findAllByPtCourseId(command.ptCourseId())
                    .stream().map(PtCurriculum::getId).toList();

            // 요청에 id가 있는 항목 → 수정 (소유권: 요청 id ⊆ 기존 id)
            List<Long> requestIds = command.curriculums().stream()
                    .filter(c -> c.id() != null).map(UpdatePtCourseCommand.CurriculumData::id).toList();
            if (requestIds.size() != requestIds.stream().distinct().count()) {
                log.warn("event=pt_course_update_failed reason=duplicate_curriculum_id ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }
            if (!existingIds.containsAll(requestIds)) {
                log.warn("event=pt_course_update_failed reason=invalid_curriculum_id ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            // 요청에 없는 기존 커리큘럼 → 삭제
            List<Long> toDelete = existingIds.stream().filter(id -> !requestIds.contains(id)).toList();
            ptCurriculumRepository.deleteAllByIdIn(toDelete);

            // 수정 또는 신규 생성
            for (UpdatePtCourseCommand.CurriculumData c : command.curriculums()) {
                if (c.id() != null) {
                    ptCurriculumRepository.update(PtCurriculum.restore(c.id(), command.ptCourseId(), c.sessionNo(), c.title(), c.content()));
                } else {
                    ptCurriculumRepository.saveAll(List.of(PtCurriculum.create(command.ptCourseId(), c.sessionNo(), c.title(), c.content())));
                }
            }
        }

        // 스케줄 upsert
        if (command.schedules() != null) {
            // 빈 리스트 = 스케줄 전체 삭제 → 최소 1개 필수
            if (command.schedules().isEmpty()) {
                log.warn("event=pt_course_update_failed reason=no_schedule ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            // 스케줄 슬롯 중복 검증 (생성과 동일 규칙)
            long distinctSchedule = command.schedules().stream()
                    .map(s -> normalizeScheduleKey(s.dayOfWeek(), s.startTime(), s.endTime()))
                    .distinct().count();
            if (distinctSchedule != command.schedules().size()) {
                log.warn("event=pt_course_update_failed reason=duplicate_schedule ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            List<Long> existingScheduleIds = ptCourseScheduleRepository.findAllByPtCourseId(command.ptCourseId())
                    .stream().map(PtCourseSchedule::getId).toList();

            List<Long> requestScheduleIds = command.schedules().stream()
                    .filter(s -> s.id() != null).map(UpdatePtCourseCommand.ScheduleData::id).toList();
            if (requestScheduleIds.size() != requestScheduleIds.stream().distinct().count()) {
                log.warn("event=pt_course_update_failed reason=duplicate_schedule_id ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            // 소유권: 요청 id ⊆ 기존 id
            if (!existingScheduleIds.containsAll(requestScheduleIds)) {
                log.warn("event=pt_course_update_failed reason=invalid_schedule_id ptCourseId={}", command.ptCourseId());
                throw new PtCourseRequestInvalidException();
            }

            List<Long> toDeleteSchedules = existingScheduleIds.stream().filter(id -> !requestScheduleIds.contains(id)).toList();
            ptCourseScheduleRepository.deleteAllByIdIn(toDeleteSchedules);

            for (UpdatePtCourseCommand.ScheduleData s : command.schedules()) {
                if (s.id() != null) {
                    // update() 사용 — 파싱·시간순 검증 포함
                    ptCourseScheduleRepository.update(
                            PtCourseSchedule.update(s.id(), command.ptCourseId(), s.dayOfWeek(), s.startTime(), s.endTime())
                    );
                } else {
                    ptCourseScheduleRepository.saveAll(List.of(
                            PtCourseSchedule.create(command.ptCourseId(), s.dayOfWeek(), s.startTime(), s.endTime())
                    ));
                }
            }
        }

        log.info("event=pt_course_update_succeeded ptCourseId={}", command.ptCourseId());
        eventPublisher.publishEvent(new PtCourseListCacheEvictEvent());
        return command.ptCourseId();
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
        Long trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(command.userId());
        if (!ptCourse.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_course_status_change_failed, reason=forbidden, userId={}, ptCourseId={}",
                    command.userId(), command.ptCourseId());
            throw new PtCourseForbiddenException();
        }

        ptCourse.changeStatus(command.status());
        ptCourseRepository.update(ptCourse);
        log.info("event=pt_course_status_change_succeeded, ptCourseId={}, status={}",
                command.ptCourseId(), command.status());
        eventPublisher.publishEvent(new PtCourseListCacheEvictEvent());
    }

    // PT 강습 삭제
    @Override
    public void deletePtCourse(DeletePtCourseCommand command) {
        log.debug("event=pt_course_delete_started userId={} ptCourseId={}",
                command.userId(), command.ptCourseId());

        // PT 존재 여부 확인
        PtCourse ptCourse = ptCourseRepository.findById(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("event=pt_course_delete_failed reason=not_found ptCourseId={}", command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

        // 이미 삭제된 강습은 404 처리
        if (ptCourse.getStatus() == PtCourseStatus.DELETED) {
            log.warn("event=pt_course_delete_failed reason=already_deleted ptCourseId={}", command.ptCourseId());
            throw new PtCourseNotFoundException();
        }

        // 본인 강습 여부 확인
        Long trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(command.userId());
        if (!ptCourse.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_course_delete_failed reason=forbidden userId={} ptCourseId={}", command.userId(), command.ptCourseId());
            throw new PtCourseForbiddenException();
        }

        // 활성 예약 존재 시 삭제 거부
        int activeCount = ptReservationCountQueryPort.countActiveByPtCourseIds(
                List.of(command.ptCourseId()))
                .getOrDefault(command.ptCourseId(), 0);
        if (activeCount > 0) {
            log.warn("event=pt_course_delete_failed reason=has_active_reservation ptCourseId={} activeCount={}", command.ptCourseId(), activeCount);
            throw new PtCourseHasActiveReservationException();
        }

        ptCourse.delete();
        ptCourseRepository.update(ptCourse);

        log.info("event=pt_course_delete_succeeded ptCourseId={}", command.ptCourseId());
        eventPublisher.publishEvent(new PtCourseListCacheEvictEvent());
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
