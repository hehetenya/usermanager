package com.tetiana.usermanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private LocalDateTime timestamp;

    private String status;

    private int statusCode;

    private List<String> messages;

    private String path;
}
