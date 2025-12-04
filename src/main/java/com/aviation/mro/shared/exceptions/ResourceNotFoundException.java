package com.aviation.mro.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException withResource(String resource, Object id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }

    public static ResourceNotFoundException withResource(String resource, String name) {
        return new ResourceNotFoundException(resource + " not found: " + name);
    }
}
