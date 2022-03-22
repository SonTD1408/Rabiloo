package com.namtg.egovernment.service.field;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.namtg.egovernment.dto.field.FieldAndListNews;
import com.namtg.egovernment.dto.field.FieldDto;
import com.namtg.egovernment.dto.field.SubFieldDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.field.FieldEntity;
import com.namtg.egovernment.entity.field.SubFieldEntity;
import com.namtg.egovernment.entity.news.NewsEntity;
import com.namtg.egovernment.repository.field.FieldRepository;
import com.namtg.egovernment.service.news.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FieldService {
    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private SubFieldService subFieldService;

    @Autowired
    private NewsService newsService;

    public List<FieldEntity> getList() {
        return fieldRepository.findByIsDeletedFalse();
    }

    public Page<FieldEntity> getPage(String keyword, Pageable pageable) {
        Page<FieldEntity> pageResult = fieldRepository.getPage(keyword, pageable);
        List<Long> listFieldId = pageResult.getContent()
                .stream()
                .map(FieldEntity::getId)
                .collect(Collectors.toList());
        Map<Long, List<SubFieldEntity>> mapListSubFieldNameByFieldId = subFieldService.getMapSubFieldNameByFieldId(listFieldId);

        pageResult.forEach(fieldEntity -> {
            fieldEntity.setListSubField(mapListSubFieldNameByFieldId.get(fieldEntity.getId()));
        });
        return pageResult;
    }

    @Transactional
    public ServerResponseDto save(FieldDto fieldDto) {
        Long id = fieldDto.getId();
        boolean isUpdate = id != null;

        FieldEntity fieldEntity;
        if (isUpdate) {
            fieldEntity = fieldRepository.findByIdAndIsDeletedFalse(id);
        } else {
            fieldEntity = new FieldEntity();
            fieldEntity.setCreatedTime(new Date());
        }
        fieldEntity.setUpdatedTime(new Date());
        fieldEntity.setName(fieldDto.getName());

        fieldEntity = fieldRepository.save(fieldEntity);

        /* save list sub_field */
        List<SubFieldDto> listSubField = fieldDto.getListSubField();
        subFieldService.saveListSubFiled(listSubField, fieldEntity.getId());

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public ServerResponseDto detail(Long id) {
        if (id == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        FieldEntity fieldEntity = fieldRepository.findByIdAndIsDeletedFalse(id);
        List<SubFieldEntity> listSubField = subFieldService.findByFieldId(fieldEntity.getId());
        fieldEntity.setListSubField(listSubField);
        return new ServerResponseDto(ResponseCase.SUCCESS, fieldEntity);
    }

    public ServerResponseDto delete(Long id) {
        if (id == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        FieldEntity fieldEntity = fieldRepository.findByIdAndIsDeletedFalse(id);
        fieldEntity.setDeleted(true);
        fieldRepository.save(fieldEntity);

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public Map<Long, String> getMapFieldNameBySubFieldId(List<Long> listSubFieldId) {
        if (listSubFieldId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SubFieldEntity> listSubField = subFieldService.findByIdInAndIsDeletedFalse(listSubFieldId);
        List<Long> listFieldId = listSubField
                .stream()
                .map(SubFieldEntity::getFieldId)
                .collect(Collectors.toList());
        Map<Long, String> mapFieldNameByFieldId = getMapFieldNameByFieldId(listFieldId);
        Map<Long, Long> mapFieldIdBySubFieldId = getMapFieldIdBySubFieldId(listSubField);

        Map<Long, String> mapResult = Maps.newHashMapWithExpectedSize(listSubFieldId.size());
        listSubFieldId.forEach(subFieldId -> {
            mapResult.put(subFieldId, mapFieldNameByFieldId.get(mapFieldIdBySubFieldId.get(subFieldId)));
        });

        return mapResult;
    }

    private Map<Long, String> getMapFieldNameByFieldId(List<Long> listFieldId) {
        if (listFieldId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<FieldEntity> listField = fieldRepository.findByIdInAndIsDeletedFalse(listFieldId);
        return listField
                .stream()
                .collect(Collectors.toMap(FieldEntity::getId, FieldEntity::getName));
    }

    public List<FieldAndListNews> getListDataFieldAndNews() {
        List<FieldEntity> listField = fieldRepository.findByIsDeletedFalse();
        List<SubFieldEntity> listSubField = subFieldService.findByIsDeletedFalse();
        Map<Long, List<SubFieldEntity>> mapListSubFieldByFieldId = listSubField
                .stream()
                .collect(Collectors.groupingBy(SubFieldEntity::getFieldId));
        List<NewsEntity> listNews = newsService.getListNews();
        Map<Long, List<NewsEntity>> mapListNewsBySubFieldId = listNews
                .stream()
                .collect(Collectors.groupingBy(NewsEntity::getSubFieldId));

        List<FieldAndListNews> listResult = Lists.newArrayListWithCapacity(listField.size());

        Map<Long, List<Long>> mapListSubFieldIdByFieldId = getMapListSubFiledIdByFieldId(listSubField);
        listField.forEach(field -> {
            FieldAndListNews dataItem = new FieldAndListNews();
            List<Long> listSubFieldId = mapListSubFieldIdByFieldId.get(field.getId());
            List<NewsEntity> listNewsEntity = new ArrayList<>();

            for (Long subFieldId : listSubFieldId) {
                listNewsEntity.addAll(mapListNewsBySubFieldId.getOrDefault(subFieldId, Collections.emptyList()));
            }

            if (listNewsEntity.size() > 1) {
                listNewsEntity = listNewsEntity
                        .stream()
                        .sorted(Comparator.comparing(NewsEntity::getCreatedTime).reversed())
                        .collect(Collectors.toList());
                dataItem.setNewsLatest(listNewsEntity.get(0));
                dataItem.setNewsLatestTop2(listNewsEntity.get(1));
                dataItem.setField(field);
                listNewsEntity.remove(1);
                listNewsEntity.remove(0);
                dataItem.setListNews(listNewsEntity);
                dataItem.setListSubField(mapListSubFieldByFieldId.get(field.getId()));
                listResult.add(dataItem);
            }
        });

        return listResult;
    }

    private Map<Long, List<Long>> getMapListSubFiledIdByFieldId(List<SubFieldEntity> listSubField) {
        if (listSubField.isEmpty()) {
            return Collections.emptyMap();
        }
        return listSubField
                .stream()
                .collect(Collectors.groupingBy(SubFieldEntity::getFieldId, Collectors.mapping(SubFieldEntity::getId, Collectors.toList())));
    }

    private Map<Long, Long> getMapFieldIdBySubFieldId(List<SubFieldEntity> listSubField) {
        if (listSubField.isEmpty()) {
            return Collections.emptyMap();
        }
        return listSubField
                .stream()
                .collect(Collectors.toMap(SubFieldEntity::getId, SubFieldEntity::getFieldId));
    }
}
