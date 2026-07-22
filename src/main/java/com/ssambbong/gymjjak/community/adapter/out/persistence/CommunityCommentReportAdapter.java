package com.ssambbong.gymjjak.community.adapter.out.persistence;

import com.ssambbong.gymjjak.community.adapter.out.persistence.entity.CommunityCommentJpaEntity;
import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityCommentRepository;
import com.ssambbong.gymjjak.community.domain.exception.CommunityErrorCode;
import com.ssambbong.gymjjak.community.domain.exception.CommunityException;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.community.CommentReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.community.CommentSanctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ssambbong.gymjjak.report.application.port.ReportSanctionAction.*;

@Component
@RequiredArgsConstructor
public class CommunityCommentReportAdapter
        implements CommentReportTargetPort, CommentSanctionPort {

    // 댓글 신고 스냅샷
    private static final String COMMENT_REPORT_TITLE = "댓글";

    private final SpringDataCommunityCommentRepository
            springDataCommunityCommentRepository;

    @Override
    public ReportTargetSnapshot getSnapshot(
            Long targetId
    ) {

        CommunityCommentJpaEntity communityComment =
                springDataCommunityCommentRepository
                        .findByIdAndDeletedAtIsNull(
                                targetId
                        )
                        .orElseThrow(
                                () -> new CommunityException(
                                        CommunityErrorCode
                                                .COMMUNITY_COMMENT_NOT_FOUND
                                )
                        );

        return new ReportTargetSnapshot(
                communityComment.getId(),
                communityComment.getUserId(),
                COMMENT_REPORT_TITLE,
                communityComment.getContent(),
                null
        );
    }

    @Override
    public void applySanction(
            Long targetId,
            ReportSanctionAction action
    ) {

        int affectedRowCount = switch (action) {

            case APPLY_AUTO_BLIND,
                 APPLY_MANUAL_BLIND ->
                    springDataCommunityCommentRepository
                            .softDeleteCommunityCommentById(
                                    targetId
                            );

            case RELEASE_AUTO_BLIND ->
                    springDataCommunityCommentRepository
                            .restoreCommunityCommentById(
                                    targetId
                            );
        };

        if (affectedRowCount == 0) {
            throw new CommunityException(
                    CommunityErrorCode
                            .COMMUNITY_COMMENT_NOT_FOUND
            );
        }
    }
}
