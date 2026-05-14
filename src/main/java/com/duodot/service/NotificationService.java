package com.duodot.service;

import com.duodot.dto.NotificationDTO;
import com.duodot.entity.Notification;
import com.duodot.entity.User;
import com.duodot.repository.NotificationRepository;
import com.duodot.responseBean.ServiceResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public ServiceResponseBean getNotifications(ServiceResponseBean serviceResponseBean) {
        try {
            User currentUser = userService.getCurrentUser();
            List<Notification> notifications = notificationRepository
                    .findByReceiverIdOrderByCreatedDateDesc(currentUser.getUserId());
            List<NotificationDTO> result = notifications.stream()
                    .map(n -> modelMapper.map(n, NotificationDTO.class))
                    .collect(Collectors.toList());
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Notifications retrieved");
            serviceResponseBean.setData(result);
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }
}
