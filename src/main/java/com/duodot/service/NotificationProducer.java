package com.duodot.service;

import com.duodot.config.RabbitMQConfig;
import com.duodot.requestBean.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationMessage message) {
        try {
            log.info("Publishing notification type={} to receiverId={}", message.getType(), message.getReceiverId());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("Failed to publish notification message :: {}", e.getMessage());
        }
    }
}
