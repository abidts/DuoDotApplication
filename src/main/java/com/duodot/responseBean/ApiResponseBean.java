package com.duodot.responseBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseBean<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponseBean<T> success(String message, T data) {
        return ApiResponseBean.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponseBean<T> error(String message) {
        return ApiResponseBean.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
