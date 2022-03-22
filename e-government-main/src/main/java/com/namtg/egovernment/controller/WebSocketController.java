package com.namtg.egovernment.controller;

import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/newNotification")
    @SendTo("/notification/admin/newNotification")
    public NotificationAdminEntity sendMessage(@Payload NotificationAdminEntity notification) {
        return notification;
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/publicChatRoom")
    public NotificationAdminEntity addUser(@Payload NotificationAdminEntity chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
