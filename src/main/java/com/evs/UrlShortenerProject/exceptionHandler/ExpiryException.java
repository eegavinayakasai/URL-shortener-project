package com.evs.UrlShortenerProject.exceptionHandler;

public class ExpiryException extends RuntimeException
{
    public ExpiryException(String message) {
        super(message);
    }
}
