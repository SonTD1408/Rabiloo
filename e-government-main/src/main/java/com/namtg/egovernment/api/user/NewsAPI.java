package com.namtg.egovernment.api.user;

import com.namtg.egovernment.service.news.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/news")
public class NewsAPI {
    @Autowired
    private NewsService newsService;

    @GetMapping("/downloadNews/{newsId}")
    public void downloadNews(@PathVariable Long newsId,
                             HttpServletResponse response) throws IOException {
        newsService.downloadNews(newsId, response);
    }
}
