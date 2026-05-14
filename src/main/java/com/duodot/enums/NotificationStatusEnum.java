package com.duodot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum NotificationStatusEnum {
    SENT("SENT"),
    FAILED("FAILED");

    @Getter
    private String value;

    public static List<String> getAllValues() {
        return List.of(NotificationStatusEnum.values()).stream().map(data -> data.value).collect(Collectors.toList());
    }
}
