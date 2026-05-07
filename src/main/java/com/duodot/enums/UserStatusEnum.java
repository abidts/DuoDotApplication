package com.duodot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
public enum UserStatusEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    VERIFICATION_PENDING("VERIFICATION PENDING");


    @Getter
    private String value;

    public static List<String> getAllValues() {
        return List.of(UserStatusEnum.values()).stream().map(data -> data.value).collect(Collectors.toList());

    }
}
