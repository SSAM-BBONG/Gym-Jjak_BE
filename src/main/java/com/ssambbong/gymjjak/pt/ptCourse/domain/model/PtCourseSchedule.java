package com.ssambbong.gymjjak.pt.ptCourse.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class PtCourseSchedule {

    private final Long id;
    private final Long ptCourseId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    // 생성자
    public PtCourseSchedule(Long id, Long ptCourseId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.ptCourseId = ptCourseId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 신규 생성 시 (String 도메인 타입으로 변환 + 시간 검증)
    public static PtCourseSchedule create(Long ptCourseId, String dayOfWeek, String startTime, String endTime) {
        DayOfWeek parsedDay;
        try {
            parsedDay = DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e) {
            throw new PtCourseInvalidException();
        }

        LocalTime parsedStart = LocalTime.parse(startTime);
        LocalTime parsedEnd = LocalTime.parse(endTime);

        if (!parsedEnd.isAfter(parsedStart)) {
            throw new PtCourseInvalidException();
        }
        return new PtCourseSchedule(null, ptCourseId, parsedDay, parsedStart, parsedEnd);
    }

    // 데이터 복원 시
    public static PtCourseSchedule restore(Long id, Long ptCourseId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return new PtCourseSchedule(id, ptCourseId, dayOfWeek, startTime, endTime);
    }

    public Long getId() {
        return id;
    }

    public Long getPtCourseId() {
        return ptCourseId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
