package com.bobcarrental.exception;

/**
 * Exception thrown when business validation fails.
 * Maps to HTTP 400 Bad Request status.
 * Used for custom validation logic beyond standard Bean Validation.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob
