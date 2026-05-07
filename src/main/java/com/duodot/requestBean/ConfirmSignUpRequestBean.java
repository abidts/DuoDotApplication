package com.duodot.requestBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmSignUpRequestBean {
    @NotBlank(message = "UserId can not be empty.")
    private String userId;

    @NotBlank(message = "OTP can not be empty.")
    @JsonProperty(value = "otp")
    private String otp;

}
