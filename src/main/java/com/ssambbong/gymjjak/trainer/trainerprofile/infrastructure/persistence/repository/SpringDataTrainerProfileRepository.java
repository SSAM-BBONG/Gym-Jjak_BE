package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface SpringDataTrainerProfileRepository extends JpaRepository<TrainerProfileJpaEntity, Long> {

    Optional<TrainerProfileJpaEntity> findByUserId(Long userId);

    @Query("""
            select new com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult(
            tp.trainerProfileId,
            u.name,
            u.username,
            u.nickname
        )
        from TrainerProfileJpaEntity tp
        join UserJpaEntity u on u.id = tp.userId
        where tp.status = :status
          and (
                :keyword is null
                or u.name like concat('%', :keyword, '%') 
                or u.username like concat('%', :keyword, '%') 
                or u.nickname like concat('%', :keyword, '%') 
          )
        order by u.name asc, tp.trainerProfileId asc
    """)
    Page<SearchTrainerResult> searchTrainers(
            @Param("status")TrainerProfileStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
