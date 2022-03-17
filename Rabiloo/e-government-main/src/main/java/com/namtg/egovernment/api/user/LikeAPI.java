package com.namtg.egovernment.api.user;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
public class LikeAPI {
    @Autowired
    private LikeService likeService;

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<ServerResponseDto> likeComment(@PathVariable Long commentId,
                                                         @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.NOT_LOGIN));
        }
        return ResponseEntity.ok(likeService.likeComment(commentId, customUserDetail.getId()));
    }

    @PostMapping("/replyComment/{replyCommentId}")
    public ResponseEntity<ServerResponseDto> likeReplyComment(@PathVariable Long replyCommentId,
                                                              @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.NOT_LOGIN));
        }
        return ResponseEntity.ok(likeService.likeReplyComment(replyCommentId, customUserDetail.getId()));
    }
}
