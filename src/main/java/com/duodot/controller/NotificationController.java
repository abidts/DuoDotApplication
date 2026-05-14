package com.duodot.controller;

import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ServiceResponseBean getNotifications() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = notificationService.getNotifications(serviceResponseBean);
        return serviceResponseBean;
    }
}
