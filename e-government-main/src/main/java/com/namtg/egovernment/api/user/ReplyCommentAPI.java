package com.namtg.egovernment.api.user;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.reply_comment.ReplyCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply_comment")
public class ReplyCommentAPI {
    @Autowired
    private ReplyCommentService replyCommentService;

    @PostMapping("/submitReplyComment")
    public ResponseEntity<ServerResponseDto> submitReplyComment(@ModelAttribute ReplyCommentDto replyCommentDto,
                                                                @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.NOT_LOGIN));
        }
        return ResponseEntity.ok(replyCommentService.submitReplyComment(replyCommentDto, customUserDetail.getId()));
    }

    @PostMapping("/delete/{replyCommentId}")
    public ResponseEntity<ServerResponseDto> deleteReplyComment(@PathVariable Long replyCommentId) {
        return ResponseEntity.ok(replyCommentService.deleteReplyComment(replyCommentId));
    }
}
