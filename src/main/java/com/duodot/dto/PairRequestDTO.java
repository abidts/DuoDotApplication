package com.duodot.dto;

import com.duodot.entity.PairRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PairRequestDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private PairRequest.RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
