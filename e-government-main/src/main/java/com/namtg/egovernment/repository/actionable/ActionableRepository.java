package com.namtg.egovernment.repository.actionable;

import com.namtg.egovernment.entity.actionable.ActionableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActionableRepository extends JpaRepository<ActionableEntity, Long> {

    ActionableEntity findByUserId(Long userId);

    @Query(value = "select a from ActionableEntity a " +
            "where a.userId = ?1")
    Optional<ActionableEntity> getOptionalByUserId(Long userId);

    @Query(value = "select a from ActionableEntity a " +
            "where a.userId in ?1")
    List<ActionableEntity> findByListUserId(List<Long> listUserId);
}
