package com.namtg.egovernment.listener;

import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userImpactId = (Long) headerAccessor.getSessionAttributes().get("currentUserId");

        if(userImpactId != null) {

            NotificationAdminEntity notification = new NotificationAdminEntity();
            notification.setUserImpactId(userImpactId);

            messagingTemplate.convertAndSend("/notification/admin", notification);
        }
    }
}
