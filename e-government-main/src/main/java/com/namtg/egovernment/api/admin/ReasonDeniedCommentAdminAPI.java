package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.dto.field.FieldDto;
import com.namtg.egovernment.dto.reason_denied_comment.ReasonDeniedCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.field.FieldEntity;
import com.namtg.egovernment.entity.reason_denied_comment.ReasonDeniedCommentEntity;
import com.namtg.egovernment.service.reason_denied_comment.ReasonDeniedCommentService;
import com.namtg.egovernment.util.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reason_denied_comment")
public class ReasonDeniedCommentAdminAPI {

    @Autowired
    private ReasonDeniedCommentService reasonDeniedCommentService;

    @GetMapping("getPage")
    public Page<ReasonDeniedCommentEntity> getPage(@RequestParam int size, @RequestParam int page,
                                                   @RequestParam String sortDir, @RequestParam String sortField,
                                                   @RequestParam String keyword) {
        Pageable pageable = PageableUtils.from(page, size, sortDir, sortField);

        return reasonDeniedCommentService.getPage(keyword, pageable);
    }

    @GetMapping("/getList")
    public ResponseEntity<ServerResponseDto> getList() {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, reasonDeniedCommentService.getList()));
    }

    @PostMapping("/save")
    public ResponseEntity<ServerResponseDto> save(@RequestBody ReasonDeniedCommentDto reasonDeniedCommentDto){
        return ResponseEntity.ok(reasonDeniedCommentService.save(reasonDeniedCommentDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(reasonDeniedCommentService.detail(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<ServerResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.ok(reasonDeniedCommentService.delete(id));
    }
}
