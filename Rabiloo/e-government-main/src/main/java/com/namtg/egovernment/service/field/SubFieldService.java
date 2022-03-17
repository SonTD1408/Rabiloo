package com.namtg.egovernment.service.field;

import com.github.slugify.Slugify;
import com.google.common.collect.Lists;
import com.namtg.egovernment.dto.field.SubFieldDto;
import com.namtg.egovernment.entity.field.SubFieldEntity;
import com.namtg.egovernment.repository.field.SubFieldRepository;
import com.namtg.egovernment.service.news.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class SubFieldService {
    @Autowired
    private SubFieldRepository subFieldRepository;

    @Autowired
    private NewsService newsService;

    public Map<Long, String> getMapSubFieldNameBySubFieldId(List<Long> listSubFieldId) {
        if (listSubFieldId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SubFieldEntity> listSubField = subFieldRepository.findByIdInAndIsDeletedFalse(listSubFieldId);
        return listSubField
                .stream()
                .collect(Collectors.toMap(SubFieldEntity::getId, SubFieldEntity::getName));
    }

    public List<SubFieldEntity> findByFieldId(Long fieldId) {
        return subFieldRepository.findByFieldIdAndIsDeletedFalse(fieldId);
    }

    public List<SubFieldEntity> findByIdInAndIsDeletedFalse(List<Long> listSubFieldId) {
        if (listSubFieldId.isEmpty()) {
            return Collections.emptyList();
        }
        return subFieldRepository.findByIdInAndIsDeletedFalse(listSubFieldId);
    }

    public Long getFieldIdBySubFieldId(Long subFieldId) {
        SubFieldEntity subFieldEntity = subFieldRepository.findByIdAndIsDeletedFalse(subFieldId);
        if (subFieldEntity == null) {
            return null;
        }
        return subFieldEntity.getFieldId();
    }

    @Transactional
    public void saveListSubFiled(List<SubFieldDto> listSubFieldDto, Long fieldId) {
        if (listSubFieldDto.isEmpty()) {
            return;
        }

        /* add new */
        List<SubFieldDto> listSubFieldDtoNew = listSubFieldDto
                .stream()
                .filter(subField -> subField.getId() == null)
                .collect(Collectors.toList());
        List<SubFieldEntity> listSubFieldEntityNew = Lists.newArrayListWithCapacity(listSubFieldDtoNew.size());

        listSubFieldDtoNew.forEach(subField -> {
            SubFieldEntity subFieldEntity = new SubFieldEntity();
            subFieldEntity.setCreatedTime(new Date());
            subFieldEntity.setName(subField.getName());
            subFieldEntity.setFieldId(fieldId);
            listSubFieldEntityNew.add(subFieldEntity);
        });
        /**/


        Set<Long> listSubFieldIdAfter = listSubFieldDto
                .stream()
                .map(SubFieldDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SubFieldEntity> listSubFieldBefore = subFieldRepository.findByFieldIdAndIsDeletedFalse(fieldId);
        List<Long> listSubFieldIdBefore = listSubFieldBefore
                .stream()
                .map(SubFieldEntity::getId)
                .collect(Collectors.toList());
        if (listSubFieldIdAfter.isEmpty()) {
            subFieldRepository.saveAll(listSubFieldEntityNew);
            return;
        }

        /* update */
        Map<Long, SubFieldDto> mapSubFieldById = listSubFieldDto
                .stream()
                .filter(subFieldDto -> subFieldDto.getId() != null)
                .collect(Collectors.toMap(SubFieldDto::getId, Function.identity()));

        List<SubFieldEntity> listSubFieldUpdate = subFieldRepository.findByIdInAndIsDeletedFalse(listSubFieldIdAfter);
        listSubFieldUpdate.forEach(subField -> {
            subField.setFieldId(fieldId);
            subField.setName(mapSubFieldById.get(subField.getId()).getName());
            subField.setUpdatedTime(new Date());
        });
        subFieldRepository.saveAll(listSubFieldUpdate);
        /**/

        /* delete */
        List<Long> listSubFieldIdBeDeleted = listSubFieldIdBefore
                .stream()
                .filter(subFieldId -> !listSubFieldIdAfter.contains(subFieldId))
                .collect(Collectors.toList());
        if (!listSubFieldIdBeDeleted.isEmpty()) {
            subFieldRepository.deleteByListSubFieldId(listSubFieldIdBeDeleted);

            // delete news
            newsService.deleteByListSubFieldId(listSubFieldIdBeDeleted);
        }
        /**/

        subFieldRepository.saveAll(listSubFieldEntityNew);
    }

    public Map<Long, List<SubFieldEntity>> getMapSubFieldNameByFieldId(List<Long> listFieldId) {
        if (listFieldId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SubFieldEntity> listSubFieldEntity = subFieldRepository.findByListFieldIdAndIsDeletedFalse(listFieldId);
        return listSubFieldEntity
                .stream()
                .collect(Collectors.groupingBy(SubFieldEntity::getFieldId));
    }

    public List<SubFieldEntity> findByIsDeletedFalse() {
        return subFieldRepository.findByIsDeletedFalse();
    }
}
