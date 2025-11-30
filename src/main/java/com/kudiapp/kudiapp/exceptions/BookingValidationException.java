package com.kudiapp.kudiapp.exceptions;

public class BookingValidationException extends RuntimeException {
    public BookingValidationException(String message) {
        super(message);
    }
}