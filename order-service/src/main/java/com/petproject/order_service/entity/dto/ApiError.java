package com.petproject.order_service.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiError {
    private int status;
    private String message;
    private Map<String, String> validationErrors;
    private LocalDateTime timestamp;
}