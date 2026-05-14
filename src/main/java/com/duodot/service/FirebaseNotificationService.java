package com.duodot.service;

import com.duodot.enums.NotificationChannelNameEnum;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseNotificationService {

    public boolean sendToTopic(String receiverUserId, String title, String body, String type) {
        try {
            String topic = NotificationChannelNameEnum.NOTIFICATION_USER.getValue().concat(receiverUserId);
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", type)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM notification sent to topic={} type={} response={}", topic, type, response);
            return true;
        } catch (Exception e) {
            log.error("Failed to send FCM notification to topic={} :: {}", receiverUserId, e.getMessage());
            return false;
        }
    }
}
