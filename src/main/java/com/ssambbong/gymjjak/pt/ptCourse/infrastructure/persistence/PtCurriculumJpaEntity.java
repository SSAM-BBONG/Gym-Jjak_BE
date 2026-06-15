package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pt_curriculums")
public class PtCurriculumJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @Column(name = "pt_curriculum_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pt_course_id", nullable = false)
    private Long ptCourseId;

    @Column(name = "session_no", nullable = false)
    private int sessionNo;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public PtCurriculumJpaEntity(Long ptCourseId, int sessionNo, String title, String content) {
        this.ptCourseId = ptCourseId;
        this.sessionNo = sessionNo;
        this.title = title;
        this.content = content;
    }
}
