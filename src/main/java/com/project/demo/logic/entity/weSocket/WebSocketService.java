package com.project.demo.logic.entity.weSocket;

import com.project.demo.logic.entity.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendGlobalNotification(Object data, String action) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                Map.of("action", action, "data", data)
        );
    }

    public void sendNotificationRemoval(Long notificationId) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                Map.of("action", "DELETE", "id", notificationId)
        );
    }

    // Método específico para marcar como leído
    public void sendMarkAsReadNotification(Long userId, Long notificationId) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                Map.of(
                        "action", "MARK_AS_READ",
                        "userId", userId,
                        "notificationId", notificationId
                )
        );
    }

    // Método específico para marcar todas como leídas
    public void sendMarkAllAsReadNotification(Long userId) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                Map.of(
                        "action", "MARK_ALL_AS_READ",
                        "userId", userId
                )
        );
    }

}
