package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.notification.NotificationAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notification")
public class NotificationAdminAPI {
    @Autowired
    private NotificationAdminService notificationAdminService;

    @GetMapping("/getListNotification")
    public ResponseEntity<ServerResponseDto> getListNotification(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, notificationAdminService
                .getListNotification(customUserDetail.getId())));
    }

    @GetMapping("/getNumberNotificationHavenWatch")
    public ResponseEntity<ServerResponseDto> getNumberNotificationHavenWatch(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            int numberNotification = 0;
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, numberNotification));
        }
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS,
                notificationAdminService.getNumberNotificationHavenWatch(customUserDetail.getId())));
    }

    @GetMapping("/setAllNotificationToWatched")
    public ResponseEntity<ServerResponseDto> setAllNotificationToWatched(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }
        notificationAdminService.setAllNotificationToWatched(customUserDetail.getId());
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS));
    }

    @GetMapping("/detail/{notificationId}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long notificationId,
                                                    @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }
        return ResponseEntity.ok(notificationAdminService.detail(notificationId, customUserDetail.getId()));
    }
}
