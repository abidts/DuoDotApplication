package com.duodot.entity;

import com.duodot.enums.NotificationStatusEnum;
import com.duodot.enums.PairNotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", unique = true)
    private String notificationId;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "receiver_id")
    private String receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PairNotificationTypeEnum type;

    @Column(name = "title")
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatusEnum status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Calendar createdDate;
}
