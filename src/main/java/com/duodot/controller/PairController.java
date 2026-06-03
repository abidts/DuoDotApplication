package com.duodot.controller;

import com.duodot.enums.PairStatusEnum;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.PairService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pairs")
@RequiredArgsConstructor
public class PairController {

    private final PairService pairService;

    @PostMapping("/request")
    public ServiceResponseBean sendPairRequest(
            @RequestParam String userId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.sendPairRequest(userId, serviceResponseBean);
        return serviceResponseBean;
    }

    @GetMapping("/requests")
    public ServiceResponseBean getRequests() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.getRequests(serviceResponseBean);
        return serviceResponseBean;
    }

    @PutMapping("/requests")
    public ServiceResponseBean updateRequestStatus(
            @RequestParam String pairId,
            @RequestParam String status
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.updateRequestStatus(pairId, status, serviceResponseBean);
        return serviceResponseBean;
    }

    @GetMapping("/partner")
    public ServiceResponseBean getPairedUser() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.getPairedUser(serviceResponseBean);
        return serviceResponseBean;
    }

    @DeleteMapping("/unpair")
    public ServiceResponseBean unpair() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.unpair(serviceResponseBean);
        return serviceResponseBean;
    }
}
