package com.duodot.responseBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserResponseBean {
    
    private String username;
    private String firstname;
    private String lastname;
    private String userId;
    private String email;
    private String countrycode;
    private String phoneNumber;
    private String profilePicture;
    private String city;
    private Boolean isPaired;
    private Boolean isDeleted;


    



}
