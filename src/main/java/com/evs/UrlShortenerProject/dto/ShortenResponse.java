package com.evs.UrlShortenerProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenResponse
{
    private String shortUrl;
    private String shortCode;
    private LocalDateTime createdDate;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private Long clickCounter;
}
