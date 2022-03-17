package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.news.NewsDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.news.NewsEntity;
import com.namtg.egovernment.service.news.NewsService;
import com.namtg.egovernment.util.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
public class NewsAdminAPI {

    @Autowired
    private NewsService newsService;

    @GetMapping("/getPage")
    public Page<NewsEntity> getPage(@RequestParam int size, @RequestParam int page,
                                    @RequestParam String sortDir, @RequestParam String sortField,
                                    @RequestParam String keyword) {
        Pageable pageable = PageableUtils.from(page, size, sortDir, sortField);
        return newsService.getPage(keyword, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<ServerResponseDto> saveNews(@ModelAttribute NewsDto newsDto,
                                                      @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        Long creatorId = customUserDetail.getId();
        return ResponseEntity.ok(newsService.saveNews(newsDto, creatorId));
    }

    @GetMapping("/detail/{newsId}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long newsId) {
        return ResponseEntity.ok(newsService.detail(newsId));
    }

    @PostMapping("/delete/{newsId}")
    public ResponseEntity<ServerResponseDto> delete(@PathVariable Long newsId) {
        return ResponseEntity.ok(newsService.delete(newsId));
    }
}
