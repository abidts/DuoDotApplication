package com.duodot.dto;

import com.duodot.enums.PairStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PairDTO {
    private Long id;
    private String pairId;
    private String senderId;
    private String receiverId;
    private PairStatusEnum status;
    private Calendar createdDate;
    private Calendar updatedDate;
}
