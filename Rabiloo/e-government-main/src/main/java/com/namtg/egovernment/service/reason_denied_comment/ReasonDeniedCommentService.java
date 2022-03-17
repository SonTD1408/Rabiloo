package com.namtg.egovernment.service.reason_denied_comment;

import com.namtg.egovernment.dto.reason_denied_comment.ReasonDeniedCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.reason_denied_comment.ReasonDeniedCommentEntity;
import com.namtg.egovernment.repository.reason_denied_comment.ReasonDeniedCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReasonDeniedCommentService {

    @Autowired
    private ReasonDeniedCommentRepository repository;

    public List<ReasonDeniedCommentEntity> getList() {
        return repository.findByIsDeletedFalse();
    }

    public String getContentReasonById(Long reasonDeniedId) {
        String contentReason = repository.getContentByIdAndIsDeletedFalse(reasonDeniedId);
        return contentReason != null ? contentReason : "";
    }

    public Page<ReasonDeniedCommentEntity> getPage(String keyword, Pageable pageable) {
        return repository.getPage(keyword, pageable);
    }

    public ServerResponseDto save(ReasonDeniedCommentDto reasonDeniedCommentDto) {
        Long id = reasonDeniedCommentDto.getId();
        boolean isUpdate = id != null;

        ReasonDeniedCommentEntity reasonDeniedComment;
        if (isUpdate) {
            reasonDeniedComment = repository.findByIdAndIsDeletedFalse(id);
        } else {
            reasonDeniedComment = new ReasonDeniedCommentEntity();
            reasonDeniedComment.setCreatedTime(new Date());
        }
        reasonDeniedComment.setUpdatedTime(new Date());
        reasonDeniedComment.setContent(reasonDeniedCommentDto.getContent());

        repository.save(reasonDeniedComment);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public ServerResponseDto detail(Long id) {
        if (id == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        return new ServerResponseDto(ResponseCase.SUCCESS, repository.findByIdAndIsDeletedFalse(id));
    }

    public ServerResponseDto delete(Long id) {
        if (id == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        ReasonDeniedCommentEntity reasonDeniedComment = repository.findByIdAndIsDeletedFalse(id);
        reasonDeniedComment.setDeleted(true);

        repository.save(reasonDeniedComment);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public Map<Long, String> getMapContentReasonDeniedByReasonId(List<Long> listReasonDeniedId) {
        if (listReasonDeniedId.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ReasonDeniedCommentEntity> listReasonDeniedComment = repository.findByIdInAndIsDeletedFalse(listReasonDeniedId);
        return listReasonDeniedComment
                .stream()
                .collect(Collectors.toMap(ReasonDeniedCommentEntity::getId, ReasonDeniedCommentEntity::getContent));
    }
}
