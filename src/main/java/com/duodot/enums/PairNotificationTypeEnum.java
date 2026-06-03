package com.duodot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum PairNotificationTypeEnum {
    REQUEST_SENT("PAIR REQUEST SENT"),
    PAIRED("PAIRED"),
    REJECTED("REJECTED"),
    ACCEPTED("ACCEPTED"),
    UNPAIRED("UNPAIRED");

    @Getter
    private String value;

    public static List<String> getAllValues() {
        return List.of(PairNotificationTypeEnum.values()).stream().map(data -> data.value).collect(Collectors.toList());
    }
}
