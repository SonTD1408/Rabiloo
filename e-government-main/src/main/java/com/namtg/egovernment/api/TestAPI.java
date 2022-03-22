package com.namtg.egovernment.api;

import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.job.DeleteCommentJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAPI {
    private static final String SCHEDULED_TASKS = "scheduledTasks";

    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;

    @Autowired
    private DeleteCommentJob deleteCommentJob;

    @PostMapping("/sendMail")
    public ResponseEntity<ServerResponseDto> sendMail() {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS));
    }

    @GetMapping(value = "/start")
    public String startSchedule() {
        System.out.println("Start cron");
        postProcessor.postProcessAfterInitialization(deleteCommentJob, SCHEDULED_TASKS);
        return "OK";
    }

    @GetMapping(value = "/stop")
    public String stopSchedule() {
        System.out.println("Stop cron");
        postProcessor.postProcessBeforeDestruction(deleteCommentJob, SCHEDULED_TASKS);
        return "OK";
    }
}
