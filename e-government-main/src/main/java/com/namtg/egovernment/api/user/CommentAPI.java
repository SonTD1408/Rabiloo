package com.namtg.egovernment.api.user;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.comment.CommentRequestDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentAPI {
    @Autowired
    private CommentService commentService;

    @PostMapping("/submitComment")
    public ResponseEntity<ServerResponseDto> submitComment(@ModelAttribute CommentRequestDto commentDto,
                                                           @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.NOT_LOGIN));
        }
        Long userId = customUserDetail.getId();
        return ResponseEntity.ok(commentService.submitComment(commentDto, userId));
    }

    @PostMapping("/delete/{commentId}")
    public ResponseEntity<ServerResponseDto> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
