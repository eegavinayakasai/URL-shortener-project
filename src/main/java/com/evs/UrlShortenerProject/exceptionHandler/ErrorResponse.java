package com.evs.UrlShortenerProject.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class ErrorResponse
{
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message)
    {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
