package com.duodot.requestBean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestBean {
    @NotBlank(message = "UserId can not be empty.")
    private String userId;

    @NotBlank(message = "Refresh token can not be empty.")
    private String refreshToken;
}
