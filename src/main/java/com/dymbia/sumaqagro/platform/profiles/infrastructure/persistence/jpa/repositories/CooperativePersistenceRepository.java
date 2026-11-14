package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CooperativePersistenceRepository extends JpaRepository<CooperativePersistenceEntity, Long> {

    Optional<CooperativePersistenceEntity> findByRuc(String ruc);

    List<CooperativePersistenceEntity> findAllByManagerUserId(Long managerUserId);

    @Query("select distinct cooperative from CooperativePersistenceEntity cooperative "
            + "join cooperative.members member where member.userId = :userId")
    List<CooperativePersistenceEntity> findAllByMemberUserId(@Param("userId") Long userId);

    boolean existsByRuc(String ruc);

    boolean existsByRucAndIdIsNot(String ruc, Long id);
}
