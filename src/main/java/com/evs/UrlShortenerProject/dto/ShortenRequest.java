package com.evs.UrlShortenerProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenRequest
{
    @NotBlank
    @URL
    @Size(max=2048)
    private String originalUrl;
    private String alias;
}
