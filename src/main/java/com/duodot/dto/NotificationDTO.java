package com.duodot.dto;

import com.duodot.enums.NotificationStatusEnum;
import com.duodot.enums.PairNotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String notificationId;
    private String senderId;
    private String receiverId;
    private PairNotificationTypeEnum type;
    private String title;
    private String body;
    private NotificationStatusEnum status;
    private Calendar createdDate;
}
