package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.object_request.ConfirmCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comment")
public class CommentAdminAPI {
    @Autowired
    private CommentService commentService;

    @PostMapping("/confirmComment/{commentId}")
    public ResponseEntity<ServerResponseDto> confirmComment(@PathVariable Long commentId,
                                                            @RequestBody ConfirmCommentDto confirmCommentDto,
                                                            @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }
        return ResponseEntity.ok(commentService.confirmComment(commentId, confirmCommentDto, customUserDetail.getId()));
    }
}
