package com.duodot.service;

import com.duodot.responseBean.MemoryResponseBean;
import com.duodot.entity.Memory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Component
@RequiredArgsConstructor
public class MemoryMapper {

    public MemoryResponseBean toResponse(Memory memory) {
        return MemoryResponseBean.builder()
                .memoryId(memory.getMemoryId())
                .pairId(memory.getPairId())
                .userId(memory.getUserId())
                .memoryDate(formatDate(memory.getMemoryDate()))
                .description(memory.getDescription())
                .location(memory.getLocation())
                .mediaFiles(memory.getMediaFiles())
                .createdDate(formatDate(memory.getCreatedAt()))
                .build();
    }

    private String formatDate(Calendar calendar) {
        if (calendar == null) return null;
        return calendar.toInstant()
                .atZone(calendar.getTimeZone().toZoneId())
                .toLocalDate()
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
