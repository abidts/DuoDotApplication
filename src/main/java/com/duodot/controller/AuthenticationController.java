package com.duodot.controller;

import com.duodot.requestBean.AuthenticationRequestBean;
import com.duodot.requestBean.ConfirmSignUpRequestBean;
import com.duodot.requestBean.RegisterRequestBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/signup")
    public ServiceResponseBean signUp(
            @Valid @RequestBody RegisterRequestBean request
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = authenticationService.signUp(request, serviceResponseBean);
        return serviceResponseBean;
    }

    @PostMapping("/confirm-signup")
    public ServiceResponseBean confirmSignUp(
            @Valid @RequestBody ConfirmSignUpRequestBean request
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = authenticationService.confirmSignUp(request, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PostMapping("/signin")
    public ServiceResponseBean signIn(
            @Valid @RequestBody AuthenticationRequestBean request
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = authenticationService.signIn(request, serviceResponseBean);
        return serviceResponseBean;
    }
}
