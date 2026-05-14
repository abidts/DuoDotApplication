package com.duodot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum PairStatusEnum {
    REQUEST_SENT("REQUEST SENT"),
    PAIRED("PAIRED"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    UNPAIRED("UNPAIRED");

    @Getter
    private String value;

    public static List<String> getAllValues() {
        return List.of(PairStatusEnum.values()).stream().map(data -> data.value).collect(Collectors.toList());
    }
}
