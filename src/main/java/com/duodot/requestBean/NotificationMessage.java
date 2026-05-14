package com.duodot.requestBean;

import com.duodot.enums.PairNotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private String senderId;
    private String receiverId;
    private String actorUsername;
    private PairNotificationTypeEnum type;
}
