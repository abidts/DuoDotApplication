package com.duodot.service;

import com.duodot.Utils.CommonUtils;
import com.duodot.constants.CommonConstants;
import com.duodot.dto.PairDTO;
import com.duodot.entity.Pair;
import com.duodot.entity.User;
import com.duodot.enums.PairNotificationTypeEnum;
import com.duodot.enums.PairStatusEnum;
import com.duodot.repository.PairRepository;
import com.duodot.repository.UserRepository;
import com.duodot.requestBean.NotificationMessage;
import com.duodot.responseBean.ServiceResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PairService {

    private final PairRepository pairRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationProducer notificationProducer;

    private boolean isAlreadyPaired(User user) {
        return user.getPairIds() != null && !user.getPairIds().isEmpty();
    }

    @Transactional
    public ServiceResponseBean sendPairRequest(String receiverUserId, ServiceResponseBean serviceResponseBean) {
        try {
            User sender = userService.getCurrentUser();
            log.info("User {} sending pair request to username={}", sender.getUserId(), receiverUserId);

            if (isAlreadyPaired(sender)) {
                serviceResponseBean.setMessage("You are already paired with someone");
                return serviceResponseBean;
            }

            Optional<User> receiverOpt = userRepository.findByUserIdAndIsDeletedFalse(receiverUserId);
            if (receiverOpt.isEmpty()) {
                serviceResponseBean.setMessage("User not found");
                return serviceResponseBean;
            }
            User receiver = receiverOpt.get();

            if (sender.getUserId().equals(receiver.getUserId())) {
                serviceResponseBean.setMessage("Cannot send pair request to yourself");
                return serviceResponseBean;
            }

            if (isAlreadyPaired(receiver)) {
                serviceResponseBean.setMessage("User is already paired");
                return serviceResponseBean;
            }

            Optional<Pair> existingPair = pairRepository.findExistingPair(sender.getUserId(), receiver.getUserId());
            if (existingPair.isPresent()) {
                serviceResponseBean.setMessage("A pair request already exists between you and this user");
                return serviceResponseBean;
            }

            String pairId = CommonConstants.INSTANCE.PAIR_PREFIX + CommonUtils.INSTANCE.uniqueIdentifier();
            Pair pair = Pair.builder()
                    .pairId(pairId)
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .status(PairStatusEnum.REQUEST_SENT)
                    .createdDate(Calendar.getInstance())
                    .build();

            pairRepository.save(pair);

            notificationProducer.publish(NotificationMessage.builder()
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .actorUsername(sender.getUsername())
                    .type(PairNotificationTypeEnum.REQUEST_SENT)
                    .build());

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Pair request sent successfully");
            serviceResponseBean.setData(mapToPairDTO(pair));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    public ServiceResponseBean getRequests(ServiceResponseBean serviceResponseBean) {
        try {
            User user = userService.getCurrentUser();
            List<Pair> pairs = pairRepository.findAllRequestsByUserId(user.getUserId());
            List<PairDTO> result = pairs.stream()
                    .map(this::mapToPairDTO)
                    .collect(Collectors.toList());
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Requests retrieved");
            serviceResponseBean.setData(result);
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean updateRequestStatus(String pairId, String statusStr, ServiceResponseBean serviceResponseBean) {
        try {
            PairStatusEnum status;
            try {
                status = PairStatusEnum.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                serviceResponseBean.setMessage("Invalid status. Allowed values: ACCEPTED, REJECTED");
                return serviceResponseBean;
            }

            if (status != PairStatusEnum.ACCEPTED && status != PairStatusEnum.REJECTED) {
                serviceResponseBean.setMessage("Invalid status. Allowed values: ACCEPTED, REJECTED");
                return serviceResponseBean;
            }

            User receiver = userService.getCurrentUser();
            log.info("User {} updating pair request id={} to status={}", receiver.getUserId(), pairId, status);

            Optional<Pair> pairOpt = pairRepository.findByPairId(pairId);
            if (pairOpt.isEmpty()) {
                serviceResponseBean.setMessage("Pair request not found");
                return serviceResponseBean;
            }
            Pair pair = pairOpt.get();

            if (!pair.getReceiverId().equals(receiver.getUserId())) {
                serviceResponseBean.setMessage("Unauthorized to update this request");
                return serviceResponseBean;
            }

            if (pair.getStatus() != PairStatusEnum.REQUEST_SENT) {
                serviceResponseBean.setMessage("Request already processed");
                return serviceResponseBean;
            }

            if (status == PairStatusEnum.ACCEPTED) {
                if (isAlreadyPaired(receiver)) {
                    serviceResponseBean.setMessage("You are already paired");
                    return serviceResponseBean;
                }

                User sender = userRepository.findByUserId(pair.getSenderId());
                if (sender == null) {
                    serviceResponseBean.setMessage("Sender not found");
                    return serviceResponseBean;
                }

                if (isAlreadyPaired(sender)) {
                    serviceResponseBean.setMessage("The request sender is already paired");
                    return serviceResponseBean;
                }

                if (sender.getPairIds() == null) sender.setPairIds(new ArrayList<>());
                if (receiver.getPairIds() == null) receiver.setPairIds(new ArrayList<>());
                sender.getPairIds().add(pair.getPairId());
                receiver.getPairIds().add(pair.getPairId());
                userRepository.save(sender);
                userRepository.save(receiver);

                notificationProducer.publish(NotificationMessage.builder()
                        .senderId(receiver.getUserId())
                        .receiverId(sender.getUserId())
                        .actorUsername(receiver.getUsername())
                        .type(PairNotificationTypeEnum.ACCEPTED)
                        .build());
            } else {
                User sender = userRepository.findByUserId(pair.getSenderId());

                notificationProducer.publish(NotificationMessage.builder()
                        .senderId(receiver.getUserId())
                        .receiverId(pair.getSenderId())
                        .actorUsername(receiver.getUsername())
                        .type(PairNotificationTypeEnum.REJECTED)
                        .build());
            }

            pair.setStatus(status);
            pair.setUpdatedDate(Calendar.getInstance());
            pairRepository.save(pair);

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage(status == PairStatusEnum.ACCEPTED ? "Pair request accepted" : "Pair request rejected");
            serviceResponseBean.setData(mapToPairDTO(pair));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    public ServiceResponseBean getPairedUser(ServiceResponseBean serviceResponseBean) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Pair> pairOpt = pairRepository.findActivePairByUserId(currentUser.getUserId());
            if (pairOpt.isEmpty()) {
                serviceResponseBean.setMessage("No active pair found");
                return serviceResponseBean;
            }
            Pair pair = pairOpt.get();
            String partnerUserId = pair.getSenderId().equals(currentUser.getUserId())
                    ? pair.getReceiverId()
                    : pair.getSenderId();
            User partner = userRepository.findByUserId(partnerUserId);
            if (partner == null) {
                serviceResponseBean.setMessage("Partner not found");
                return serviceResponseBean;
            }
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Paired user retrieved");
            serviceResponseBean.setData(userService.toUserDTO(partner));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean unpair(ServiceResponseBean serviceResponseBean) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Pair> pairOpt = pairRepository.findActivePairByUserId(currentUser.getUserId());
            if (pairOpt.isEmpty()) {
                serviceResponseBean.setMessage("No active pair found");
                return serviceResponseBean;
            }
            Pair pair = pairOpt.get();

            String partnerUserId = pair.getSenderId().equals(currentUser.getUserId())
                    ? pair.getReceiverId()
                    : pair.getSenderId();

            User user1 = userRepository.findByUserId(pair.getSenderId());
            User user2 = userRepository.findByUserId(pair.getReceiverId());

            if (user1 != null) {
                if (user1.getPairIds() != null) user1.getPairIds().remove(pair.getPairId());
                userRepository.save(user1);
            }
            if (user2 != null) {
                if (user2.getPairIds() != null) user2.getPairIds().remove(pair.getPairId());
                userRepository.save(user2);
            }

            pair.setStatus(PairStatusEnum.UNPAIRED);
            pair.setUpdatedDate(Calendar.getInstance());
            pairRepository.save(pair);

            notificationProducer.publish(NotificationMessage.builder()
                    .senderId(currentUser.getUserId())
                    .receiverId(partnerUserId)
                    .actorUsername(currentUser.getUsername())
                    .type(PairNotificationTypeEnum.UNPAIRED)
                    .build());

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Unpaired successfully");
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    private PairDTO mapToPairDTO(Pair pair) {
        return PairDTO.builder()
                .id(pair.getId())
                .pairId(pair.getPairId())
                .senderId(pair.getSenderId())
                .receiverId(pair.getReceiverId())
                .status(pair.getStatus())
                .createdDate(pair.getCreatedDate())
                .updatedDate(pair.getUpdatedDate())
                .build();
    }
}
