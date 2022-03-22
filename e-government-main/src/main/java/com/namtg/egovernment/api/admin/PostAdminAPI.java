package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.discussion_post.PostDto;
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
@RequestMapping("/api/admin/discussion_post")
public class PostAdminAPI {
    @Autowired
    private PostService postService;

    @GetMapping("/getPage")
    public Page<PostEntity> getPage(@RequestParam int size, @RequestParam int page,
                                    @RequestParam String sortDir, @RequestParam String sortField,
                                    @RequestParam String keyword) {
        Pageable pageable = PageableUtils.from(page, size, sortDir, sortField);
        return postService.getPage(keyword, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<ServerResponseDto> save(@RequestBody PostDto postDto,
                                                  @AuthenticationPrincipal CustomUserDetail customUserDetail) throws ParseException {
        return ResponseEntity.ok(postService.save(customUserDetail.getId(), postDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(postService.detail(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<ServerResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.ok(postService.delete(id));
    }

}
