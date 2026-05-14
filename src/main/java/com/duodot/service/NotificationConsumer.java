package com.duodot.service;

import com.duodot.Utils.CommonUtils;
import com.duodot.constants.CommonConstants;
import com.duodot.constants.NotificationTemplates;
import com.duodot.config.RabbitMQConfig;
import com.duodot.entity.Notification;
import com.duodot.enums.NotificationStatusEnum;
import com.duodot.repository.NotificationRepository;
import com.duodot.requestBean.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final FirebaseNotificationService firebaseNotificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(NotificationMessage message) {
        log.info("Consuming notification type={} for receiverId={}", message.getType(), message.getReceiverId());

        String title = NotificationTemplates.getTitle(message.getType());
        String body  = NotificationTemplates.getBody(message.getType(), message.getActorUsername());

        boolean sent = firebaseNotificationService.sendToTopic(
                message.getReceiverId(), title, body, message.getType().name()
        );

        if (!sent) {
            throw new RuntimeException(
                "FCM notification failed for receiverId=" + message.getReceiverId()
                + " type=" + message.getType()
            );
        }

        saveNotification(message, title, body, NotificationStatusEnum.SENT);
    }

    // Receives messages that failed all 3 retry attempts
    @RabbitListener(queues = RabbitMQConfig.DLQ)
    public void consumeDeadLetter(NotificationMessage message) {
        log.error("Notification permanently failed after retries — receiverId={} type={}",
                message.getReceiverId(), message.getType());

        String title = NotificationTemplates.getTitle(message.getType());
        String body  = NotificationTemplates.getBody(message.getType(), message.getActorUsername());

        saveNotification(message, title, body, NotificationStatusEnum.FAILED);
    }

    private void saveNotification(NotificationMessage message, String title, String body, NotificationStatusEnum status) {
        Notification notification = Notification.builder()
                .notificationId(CommonConstants.INSTANCE.NOTIFICATION_PREFIX + CommonUtils.INSTANCE.uniqueIdentifier())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .type(message.getType())
                .title(title)
                .body(body)
                .status(status)
                .createdDate(Calendar.getInstance())
                .build();

        notificationRepository.save(notification);
    }
}
