package com.kudiapp.kudiapp.exceptions;

public class PackageAlreadyExistsException extends RuntimeException {
    public PackageAlreadyExistsException(String message) {
        super(message);
    }
}