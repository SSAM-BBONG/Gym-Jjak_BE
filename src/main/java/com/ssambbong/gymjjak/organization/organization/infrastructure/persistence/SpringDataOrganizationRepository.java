package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationRepository extends JpaRepository<OrganizationJpaEntity, Long> {

    Optional<OrganizationJpaEntity> findByOrganizationAccountId(Long organizationAccountId);

    long countByStatus(OrganizationStatus status);

    @Query(
            value = """
                    SELECT new com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView(
                        o.organizationId, a.requestedLoginId, o.businessName, o.representativeName,
                        o.representativePhone, COUNT(t), o.status, o.createdAt
                    )
                    FROM OrganizationJpaEntity o
                    LEFT JOIN OrganizationApplicationJpaEntity a ON a.organizationApplicationId = o.applicationId
                    LEFT JOIN OrganizationTrainerJpaEntity t ON t.organizationId = o.organizationId AND t.removedAt IS NULL
                    GROUP BY o.organizationId, a.requestedLoginId, o.businessName, o.representativeName,
                             o.representativePhone, o.status, o.createdAt
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT o.organizationId)
                    FROM OrganizationJpaEntity o
                    """
    )
    Page<OrganizationAdminView> findAllForAdmin(Pageable pageable);
}
