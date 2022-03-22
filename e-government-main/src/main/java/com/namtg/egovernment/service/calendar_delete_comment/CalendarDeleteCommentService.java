package com.namtg.egovernment.service.calendar_delete_comment;

import com.namtg.egovernment.entity.calendar_delete_comment.CalendarDeleteCommentEntity;
import com.namtg.egovernment.repository.calendar_delete_comment.CalendarDeleteCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarDeleteCommentService {

    @Autowired
    private CalendarDeleteCommentRepository repository;

    public void createCalendar(Long commentId, Long replyCommentId, int numberDaysDelete) {
        CalendarDeleteCommentEntity calendarDelete = new CalendarDeleteCommentEntity(commentId, replyCommentId);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, numberDaysDelete);
        calendarDelete.setTimeDelete(c.getTime());

        repository.save(calendarDelete);
    }

    public List<CalendarDeleteCommentEntity> getListCalendarDeleteToday(Date now) {
        return repository.getListCalendarDeleteToday(now);
    }

    @Transactional
    public void deleteList(List<CalendarDeleteCommentEntity> listCalendarDelete) {
        List<Long> listCalendarId = listCalendarDelete
                .stream()
                .map(CalendarDeleteCommentEntity::getId)
                .collect(Collectors.toList());
        repository.deleteByListId(listCalendarId);
    }

    @Transactional
    public void deleteByCommentId(Long commentId) {
        if (commentId == null) {
            return;
        }
        repository.deleteByCommentId(commentId);
    }

    @Transactional
    public void deleteByReplyCommentId(Long replyCommentId) {
        if (replyCommentId == null) {
            return;
        }
        repository.deleteByReplyCommentId(replyCommentId);
    }

    public Map<Long, Date> getMapTimeDeleteCommentByCommentId(List<Long> listCommentId) {
        if (listCommentId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CalendarDeleteCommentEntity> listCalendarDeleteComment = repository.findByCommentIdIn(listCommentId);
        return listCalendarDeleteComment
                .stream()
                .collect(Collectors.toMap(CalendarDeleteCommentEntity::getCommentId, CalendarDeleteCommentEntity::getTimeDelete));
    }

    public Map<Long, Date> getMapTimeDeleteReplyCommentByReplyCommentId(List<Long> listReplyCommentId) {
        if (listReplyCommentId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CalendarDeleteCommentEntity> listCalendarDeleteComment = repository.findByReplyCommentIdIn(listReplyCommentId);
        return listCalendarDeleteComment
                .stream()
                .collect(Collectors.toMap(CalendarDeleteCommentEntity::getReplyCommentId, CalendarDeleteCommentEntity::getTimeDelete));
    }
}
