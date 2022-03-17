package com.namtg.egovernment.api.user;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.discussion_post.ConclusionDto;
import com.namtg.egovernment.dto.discussion_post.RequestCreatePostDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.util.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/discussion_post")
public class PostAPI {
    @Autowired
    private PostService postService;

    @GetMapping("/getPage")
    public Page<PostEntity> getList(@RequestParam("size") int size,
                                    @RequestParam("page") int page,
                                    @RequestParam String keyword,
                                    @RequestParam String orderBy,
                                    @RequestParam Long fieldId) {
        Pageable pageable = PageableUtils.from(page, size);
        return postService.getPageForUser(keyword, orderBy, fieldId, pageable);
    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long postId,
                                                    @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        Long currentUserId = customUserDetail != null ? customUserDetail.getId() : null;
        return ResponseEntity.ok(postService.detailForUser(postId, currentUserId));
    }

    @PostMapping("/plusView/{postId}")
    public ResponseEntity<ServerResponseDto> plusView(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.plusView(postId));
    }

    @PostMapping("/saveConclusion")
    public ResponseEntity<ServerResponseDto> saveConclusion(@RequestBody ConclusionDto conclusionDto) {
        return ResponseEntity.ok(postService.saveConclusion(conclusionDto));
    }

    @PostMapping("/create")
    public ResponseEntity<ServerResponseDto> requestCreate(@RequestBody RequestCreatePostDto postDto,
                                                           @AuthenticationPrincipal CustomUserDetail customUserDetail) throws ParseException {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.NOT_LOGIN));
        }
        return ResponseEntity.ok(postService.requestCreate(postDto, customUserDetail.getId()));
    }

}
