package com.namtg.egovernment.controller.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", "/home"})
    public String adminHome() {
        return "admin/admin_home";
    }

    @GetMapping("/discussion_post")
    public String adminDiscussionPost() {
        return "admin/admin_discussion_post";
    }

    @GetMapping("/discussion_post/{seo}")
    public String adminDetailDiscussionPost(@PathVariable String seo, Model model) {
        PostEntity postEntity = postService.detailForAdmin(seo);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        postEntity.setClosingDeadlineStr(sdf.format(postEntity.getClosingDeadline()));
        model.addAttribute("post", postEntity);
        return "admin/admin_detail_discussion_post";
    }

    @GetMapping("/field")
    public String adminField() {
        return "admin/admin_field";
    }

    @GetMapping("/user")
    public String adminManageUser() {
        return "admin/admin_manage_user";
    }

    @GetMapping("/reason_denied_comment")
    public String adminReasonDeniedComment() {
        return "admin/admin_reason_denied_comment";
    }

    @GetMapping("/news")
    public String adminNews() {
        return "admin/admin_news";
    }

    @GetMapping("/my_profile")
    public String adminMyProfile(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                                 Model model) {
        if (customUserDetail == null) {
            return "common/not_found";
        }
        UserEntity userEntity = userService.getById(customUserDetail.getId());
        model.addAttribute("currentUser", userEntity);
        return "admin/admin_my_profile";
    }
}
