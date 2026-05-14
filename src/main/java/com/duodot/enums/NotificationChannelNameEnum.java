package com.duodot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum NotificationChannelNameEnum {
    NOTIFICATION_USER("NOTIFICATION_USER.");

    @Getter
    private String value;
}
