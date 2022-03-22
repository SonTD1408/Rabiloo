package com.namtg.egovernment.controller.user;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.field.FieldAndListNews;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.news.NewsEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.RedirectService;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.field.FieldService;
import com.namtg.egovernment.service.news.NewsService;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private PostService postService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private RedirectService redirectService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login(@AuthenticationPrincipal CustomUserDetail currentUserLogin) {
        if (currentUserLogin == null) {
            return "common/login";
        } else {
            String urlDefault = redirectService.getUrlDefault(currentUserLogin);
            return "redirect:" + urlDefault;
        }
    }

    @RequestMapping(value = {"/register"}, method = RequestMethod.GET)
    public String register() {
        return "common/register";
    }

    @RequestMapping(value = {"/forgot-password"}, method = RequestMethod.GET)
    public String forgotPassword() {
        return "common/forgot_password";
    }

    @GetMapping("/confirmForgotPassword")
    public String confirmForgotPassword(@RequestParam("token") String token, Model model) {
        boolean isConfirmSuccess = userService.confirmForgotPassword(token);
        if (isConfirmSuccess) {
            model.addAttribute("token", token);
            return "common/set_password";
        } else {
            return "common/not_found";
        }
    }

    @RequestMapping(value = {"/", "/home"})
    public String home(Model model) {
        NewsEntity newsLatest = newsService.getNewsLatest();
        model.addAttribute("newsLatest", newsLatest != null ? newsLatest : new NewsEntity());

        List<NewsEntity> listTop5NewsLatest = newsService.getListTop5NewsLatest();
        model.addAttribute("listTop5NewsLatest", listTop5NewsLatest);

        List<PostEntity> listTop5Post = postService.getListTop5Post();
        model.addAttribute("listTop5Post", listTop5Post);

        List<FieldAndListNews> listDataFieldAndNews = fieldService.getListDataFieldAndNews();
        model.addAttribute("listDataFieldAndNews", listDataFieldAndNews);

        return "user/home";
    }

    @RequestMapping(value = {"/discussion-post", "/discussion-post/{seo}"})
    public String discussionPost(Model model, @PathVariable(value = "seo", required = false) String seo) {
        if (seo != null) {
            PostEntity postDetail = postService.findBySeoAndIsDeletedFalse(seo);
            model.addAttribute("postId", postDetail.getId());
            model.addAttribute("fieldId", postDetail.getFieldId());
        }
        PostEntity postLatest = postService.getPostLatest();
        model.addAttribute("postLatest", postLatest != null ? postLatest : new PostEntity());
        return "user/discussion_post";
    }

    @GetMapping("/my_profile")
    public String myProfile(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                            Model model) {
        if (customUserDetail == null) {
            return "common/not_found";
        }
        UserEntity userEntity = userService.getById(customUserDetail.getId());
        model.addAttribute("currentUser", userEntity);
        return "user/my_profile";
    }

    @GetMapping("/news/{seo}")
    public String detailNews(@PathVariable String seo,
                             Model model) {
        NewsEntity newsLatest = newsService.getNewsLatest();
        model.addAttribute("newsLatest", newsLatest != null ? newsLatest : new NewsEntity());

        NewsEntity newsEntity = newsService.getBySeo(seo);
        newsEntity.setCreatedTimeStr(DateUtils.convertDateToStringWithPattern(newsEntity.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("newsEntity", newsEntity);
        return "user/detail_news";
    }

}
