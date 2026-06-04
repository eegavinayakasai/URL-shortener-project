package com.evs.UrlShortenerProject.exceptionHandler;

public class DuplicateResourceException extends RuntimeException
{
    public DuplicateResourceException(String message)
    {
        super(message);
    }

}
