package com.namtg.egovernment.job;

import com.google.common.collect.Lists;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.EmailService;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class NotifyConclusionPostJob {
    @Autowired
    private EmailService emailService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "${job.notifyConclusionPost}")
    public void notifyConclusionPost() {
        List<PostEntity> listPost = postService.findByIsDeletedFalse();
        Date now = new Date();

        List<String> listTitlePostMustConclusion = Lists.newArrayListWithCapacity(listPost.size());
        listPost.forEach(post -> {
            if (post.getClosingDeadline().equals(now)) {
                listTitlePostMustConclusion.add(post.getTitle());
            }
        });

        if (listTitlePostMustConclusion.isEmpty()) {
            return;
        }
        sendMailNotifyConclusionPostForAdmin(listTitlePostMustConclusion);
    }

    private void sendMailNotifyConclusionPostForAdmin(List<String> listTitlePostMustConclusion) {
        List<UserEntity> listAdminReceiveNotificationConclusion = userService.getListAdminCanReceiveNotificationConclusion();
        if (listAdminReceiveNotificationConclusion.isEmpty()) {
            return;
        }

        listAdminReceiveNotificationConclusion.forEach(admin -> {
            emailService.sendMailNotifyConclusionPost(admin.getEmail(), listTitlePostMustConclusion);
        });
    }
}
