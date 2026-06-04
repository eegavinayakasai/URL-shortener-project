package com.evs.UrlShortenerProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QrRequest
{
    @NotBlank
    @Size(max=2048)
    private String OriginalUrl;
}
