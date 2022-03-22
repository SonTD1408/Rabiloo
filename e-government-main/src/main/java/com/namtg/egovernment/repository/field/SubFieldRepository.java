package com.namtg.egovernment.repository.field;

import com.namtg.egovernment.entity.field.SubFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface SubFieldRepository extends JpaRepository<SubFieldEntity, Long> {
    List<SubFieldEntity> findByIdInAndIsDeletedFalse(Collection<Long> listSubFieldId);

    List<SubFieldEntity> findByFieldIdAndIsDeletedFalse(Long fieldId);

    SubFieldEntity findByIdAndIsDeletedFalse(Long subFieldId);

    @Query(value = "select sf.id from SubFieldEntity sf " +
            "where sf.fieldId = ?1 and sf.id in ?2 and sf.isDeleted = false")
    List<Long> getListSubFieldIdBeDeleted(Long fieldId, List<Long> listSubFieldIdSaved);

    @Modifying
    @Query(value = "delete SubFieldEntity sf " +
            "where sf.id in ?1")
    void deleteByListSubFieldId(List<Long> listSubFieldIdBeDeleted);

    @Query(value = "select sf from SubFieldEntity sf " +
            "where sf.fieldId in ?1 and sf.isDeleted = false")
    List<SubFieldEntity> findByListFieldIdAndIsDeletedFalse(List<Long> listFieldId);

    List<SubFieldEntity> findByIsDeletedFalse();
}
