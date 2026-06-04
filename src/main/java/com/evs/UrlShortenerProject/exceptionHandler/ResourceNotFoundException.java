package com.evs.UrlShortenerProject.exceptionHandler;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
