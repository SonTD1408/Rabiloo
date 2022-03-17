package com.namtg.egovernment.job;

import com.google.common.collect.Lists;
import com.namtg.egovernment.entity.calendar_delete_comment.CalendarDeleteCommentEntity;
import com.namtg.egovernment.service.calendar_delete_comment.CalendarDeleteCommentService;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DeleteCommentJob {

    @Autowired
    private CalendarDeleteCommentService calendarDeleteCommentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyCommentService replyCommentService;

    @Scheduled(cron = "${job.deleteComment}")
    public void deleteComment() {
        List<CalendarDeleteCommentEntity> listCalendarDelete = calendarDeleteCommentService.getListCalendarDeleteToday(new Date());
        if (listCalendarDelete.isEmpty()) {
            return;
        }
        List<Long> listCommentIdDeleted = Lists.newArrayListWithCapacity(listCalendarDelete.size());
        List<Long> listReplyCommentIdDeleted = Lists.newArrayListWithCapacity(listCalendarDelete.size());

        listCalendarDelete.forEach(item -> {
            Long commentId = item.getCommentId();
            Long replyCommentId = item.getReplyCommentId();
            if (commentId != null) {
                listCommentIdDeleted.add(commentId);
            }
            if (replyCommentId != null) {
                listReplyCommentIdDeleted.add(replyCommentId);
            }
        });

        commentService.deleteByListId(listCommentIdDeleted);
        replyCommentService.deleteByListId(listReplyCommentIdDeleted);

        calendarDeleteCommentService.deleteList(listCalendarDelete);
    }
}
