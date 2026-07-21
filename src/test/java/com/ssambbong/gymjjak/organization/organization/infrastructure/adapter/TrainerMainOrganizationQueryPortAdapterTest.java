package com.ssambbong.gymjjak.organization.organization.infrastructure.adapter;

import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.OrganizationJpaEntity;
import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence.SpringDataOrganizationTrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerMainOrganizationQueryPortAdapterTest {

    @Mock
    private SpringDataOrganizationTrainerRepository organizationTrainerRepository;

    @Mock
    private SpringDataOrganizationRepository organizationRepository;

    @InjectMocks
    private TrainerMainOrganizationQueryPortAdapter adapter;

    @Test
    void countActiveOrganizations_delegatesDistinctActiveOrganizationCount() {
        when(organizationTrainerRepository.countDistinctActiveOrganizationsByTrainerProfileId(10L))
                .thenReturn(2L);

        long result = adapter.countActiveOrganizations(10L);

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void findOrganizationNamesByIds_returnsBusinessNamesFromSingleBatchQuery() {
        OrganizationJpaEntity first = mock(OrganizationJpaEntity.class);
        OrganizationJpaEntity second = mock(OrganizationJpaEntity.class);
        when(first.getOrganizationId()).thenReturn(1L);
        when(first.getBusinessName()).thenReturn("짐잭 강남점");
        when(second.getOrganizationId()).thenReturn(2L);
        when(second.getBusinessName()).thenReturn("짐잭 역삼점");
        when(organizationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(first, second));

        Map<Long, String> result = adapter.findOrganizationNamesByIds(List.of(1L, 2L));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                1L, "짐잭 강남점",
                2L, "짐잭 역삼점"
        ));
        verify(organizationRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void findOrganizationNamesByIds_skipsRepositoryQueryWhenIdsAreEmpty() {
        assertThat(adapter.findOrganizationNamesByIds(List.of())).isEmpty();

        verifyNoInteractions(organizationRepository);
    }
}
