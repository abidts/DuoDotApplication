package com.duodot.controller;

import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.PairService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pairs")
@RequiredArgsConstructor
public class PairController {
    
    private final PairService pairService;
    
    @PostMapping("/request")
    public ServiceResponseBean sendPairRequest(
            @RequestParam String username
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.sendPairRequest(username, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping("/requests/pending")
    public ServiceResponseBean getPendingRequests() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.getPendingRequests(serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PutMapping("/requests/{requestId}/accept")
    public ServiceResponseBean acceptRequest(
            @PathVariable Long requestId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.acceptPairRequest(requestId, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PutMapping("/requests/{requestId}/reject")
    public ServiceResponseBean rejectRequest(
            @PathVariable Long requestId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = pairService.rejectPairRequest(requestId, serviceResponseBean);
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
