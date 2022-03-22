package com.namtg.egovernment.repository.field;

import com.namtg.egovernment.entity.field.FieldEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldRepository extends JpaRepository<FieldEntity, Long> {
    List<FieldEntity> findByIsDeletedFalse();

    @Query(value = "select f from FieldEntity f " +
            "where f.name like concat ('%', ?1, '%') and f.isDeleted = false")
    Page<FieldEntity> getPage(String keyword, Pageable pageable);

    FieldEntity findByIdAndIsDeletedFalse(Long id);

    List<FieldEntity> findByIdInAndIsDeletedFalse(List<Long> listFieldId);
}
