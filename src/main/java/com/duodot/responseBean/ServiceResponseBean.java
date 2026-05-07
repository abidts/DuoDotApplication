package com.duodot.responseBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseBean {

    private String message;

    private Boolean status = Boolean.FALSE;

    private Object data;

    private Set<String> errors;
}
