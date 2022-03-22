package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.object_request.ConfirmCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/replyComment")
public class ReplyCommentAdminAPI {
    @Autowired
    private ReplyCommentService replyCommentService;

    @PostMapping("/confirmReplyComment/{replyCommentId}")
    public ResponseEntity<ServerResponseDto> confirmReplyComment(@PathVariable Long replyCommentId,
                                                                 @RequestBody ConfirmCommentDto confirmCommentDto,
                                                                 @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }
        return ResponseEntity.ok(replyCommentService.confirmReplyComment(replyCommentId, confirmCommentDto, customUserDetail.getId()));
    }
}
