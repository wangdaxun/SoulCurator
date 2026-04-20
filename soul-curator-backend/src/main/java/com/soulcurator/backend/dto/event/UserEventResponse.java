package com.soulcurator.backend.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户事件响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEventResponse {
    
    private Boolean success;
    private String message;
    private LocalDateTime serverTimestamp;
    private Long eventId;
    private Long processingTimeMs;
    
    public UserEventResponse() {
        this.serverTimestamp = LocalDateTime.now();
    }
    
    public static UserEventResponse success(Long eventId) {
        UserEventResponse response = new UserEventResponse();
        response.setSuccess(true);
        response.setMessage("事件记录成功");
        response.setEventId(eventId);
        return response;
    }
    
    public static UserEventResponse success(Long eventId, Long processingTimeMs) {
        UserEventResponse response = success(eventId);
        response.setProcessingTimeMs(processingTimeMs);
        return response;
    }
    
    public static UserEventResponse error(String message) {
        UserEventResponse response = new UserEventResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}