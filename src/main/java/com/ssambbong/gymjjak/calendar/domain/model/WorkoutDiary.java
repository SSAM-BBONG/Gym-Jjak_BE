package com.ssambbong.gymjjak.calendar.domain.model;

import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;

import java.time.LocalDate;

public class WorkoutDiary {

    private final Long id;
    private final Long userId;
    private final Long feedbackId;
    private final String title;
    private final String content;
    private final LocalDate diaryDate;

    private WorkoutDiary(
            Long id,
            Long userId,
            Long feedbackId,
            String title,
            String content,
            LocalDate diaryDate
    ) {
        this.id = id;
        this.userId = userId;
        this.feedbackId = feedbackId;
        this.title = validateTitle(title);
        this.content = validateContent(content);
        this.diaryDate = validateDiaryDate(diaryDate);

        if (userId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }
    }

    public static WorkoutDiary create(
            Long userId,
            String title,
            String content,
            LocalDate diaryDate
    ) {
        return new WorkoutDiary(null, userId, null, title, content, diaryDate);
    }

    private static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new CalendarException(CalendarErrorCode.DIARY_TITLE_REQUIRED);
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() > 100) {
            throw new CalendarException(CalendarErrorCode.DIARY_TITLE_TOO_LONG);
        }
        return trimmedTitle;
    }

    private static String validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new CalendarException(CalendarErrorCode.DIARY_CONTENT_REQUIRED);
        }
        return content.trim();
    }

    private static LocalDate validateDiaryDate(LocalDate diaryDate) {
        if (diaryDate == null) {
            throw new CalendarException(CalendarErrorCode.DIARY_DATE_REQUIRED);
        }
        return diaryDate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getFeedbackId() { return feedbackId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDate getDiaryDate() { return diaryDate; }
}
