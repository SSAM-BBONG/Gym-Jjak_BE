package com.ssambbong.gymjjak.community;

import com.ssambbong.gymjjak.community.application.port.out.CommunityRetentionPort;
import com.ssambbong.gymjjak.community.application.retention.CommunityRetentionProperties;
import com.ssambbong.gymjjak.community.application.retention.CommunityRetentionService;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CommunityRetentionServiceTest {

    private final CommunityRetentionProperties properties =
            new CommunityRetentionProperties(90, 500);

    private final CommunityRetentionPort communityRetentionPort =
            mock(CommunityRetentionPort.class);

    private final CommunityRetentionService service =
            new CommunityRetentionService(properties, communityRetentionPort);

    @Test
    @DisplayName("expired soft-deleted comments and posts are hard deleted")
    void hardDeleteExpired_success() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        List<Long> commentIds = List.of(1L, 2L);
        List<Long> postIds = List.of(10L, 11L);

        when(communityRetentionPort.findHardDeleteCandidateCommentIds(threshold, 500))
                .thenReturn(commentIds);
        when(communityRetentionPort.hardDeleteCommentsByIds(commentIds))
                .thenReturn(2);
        when(communityRetentionPort.findHardDeleteCandidatePostIds(threshold, 500))
                .thenReturn(postIds);
        when(communityRetentionPort.hardDeletePostViewsByPostIds(postIds))
                .thenReturn(3);
        when(communityRetentionPort.hardDeletePostLikesByPostIds(postIds))
                .thenReturn(4);
        when(communityRetentionPort.hardDeleteCommentsByPostIds(postIds))
                .thenReturn(5);
        when(communityRetentionPort.hardDeletePostsByIds(postIds))
                .thenReturn(2);

        RetentionJobResult result = service.hardDeleteExpired(now);

        assertThat(result.jobName()).isEqualTo("community-retention");
        assertThat(result.candidateCount()).isEqualTo(4);
        assertThat(result.deletedChildCount()).isEqualTo(14);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        InOrder inOrder = inOrder(communityRetentionPort);
        inOrder.verify(communityRetentionPort).hardDeleteCommentsByIds(commentIds);
        inOrder.verify(communityRetentionPort).hardDeletePostViewsByPostIds(postIds);
        inOrder.verify(communityRetentionPort).hardDeletePostLikesByPostIds(postIds);
        inOrder.verify(communityRetentionPort).hardDeleteCommentsByPostIds(postIds);
        inOrder.verify(communityRetentionPort).hardDeletePostsByIds(postIds);
    }

    @Test
    @DisplayName("no delete query runs when there are no candidates")
    void hardDeleteExpired_empty() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(communityRetentionPort.findHardDeleteCandidateCommentIds(threshold, 500))
                .thenReturn(List.of());
        when(communityRetentionPort.findHardDeleteCandidatePostIds(threshold, 500))
                .thenReturn(List.of());

        RetentionJobResult result = service.hardDeleteExpired(now);

        assertThat(result.jobName()).isEqualTo("community-retention");
        assertThat(result.candidateCount()).isZero();
        assertThat(result.deletedChildCount()).isZero();
        assertThat(result.deletedParentCount()).isZero();

        verify(communityRetentionPort, never()).hardDeleteCommentsByIds(anyList());
        verify(communityRetentionPort, never()).hardDeletePostViewsByPostIds(anyList());
        verify(communityRetentionPort, never()).hardDeletePostLikesByPostIds(anyList());
        verify(communityRetentionPort, never()).hardDeleteCommentsByPostIds(anyList());
        verify(communityRetentionPort, never()).hardDeletePostsByIds(anyList());
    }
}
