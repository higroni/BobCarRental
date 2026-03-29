package com.bobcarrental.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Detalji o grešci
 * Koristi se u ApiResponse za detaljne informacije o greškama
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    
    /**
     * Kod greške (npr. VALIDATION_ERROR, NOT_FOUND, etc.)
     */
    private String code;
    
    /**
     * Detaljna poruka o grešci
     */
    private String message;
    
    /**
     * Lista validacionih grešaka (za forme)
     */
    private List<FieldError> fieldErrors;
    
    /**
     * Dodatne informacije o grešci
     */
    private Map<String, Object> details;
    
    /**
     * Stack trace (samo za development)
     */
    private String stackTrace;
    
    /**
     * Timestamp kada je greška nastala
     */
    private LocalDateTime timestamp;
    
    /**
     * Request path gde je greška nastala
     */
    private String path;
    
    /**
     * Kreira jednostavnu grešku
     */
    public static ErrorDetails of(String code, String message) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .build();
    }
    
    /**
     * Kreira grešku sa validacionim greškama
     */
    public static ErrorDetails withFieldErrors(String code, String message, List<FieldError> fieldErrors) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .fieldErrors(fieldErrors)
                .build();
    }
}

// Made with Bob
