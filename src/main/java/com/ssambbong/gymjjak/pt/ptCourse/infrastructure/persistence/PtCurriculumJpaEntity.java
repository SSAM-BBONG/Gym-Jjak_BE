package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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

    // 신규 커리큘럼 생성 시
    public PtCurriculumJpaEntity(Long ptCourseId, int sessionNo, String title, String content) {
        this.ptCourseId = ptCourseId;
        this.sessionNo = sessionNo;
        this.title = title;
        this.content = content;
    }

    // 커리큘럼 내용 수정
    public void updateFields(int sessionNo, String title, String content) {
        this.sessionNo = sessionNo;
        this.title = title;
        this.content = content;
    }
}
