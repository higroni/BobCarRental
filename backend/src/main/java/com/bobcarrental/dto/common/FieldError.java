package com.bobcarrental.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Predstavlja grešku validacije za pojedinačno polje
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {
    
    /**
     * Ime polja koje ima grešku
     */
    private String field;
    
    /**
     * Poruka o grešci
     */
    private String message;
    
    /**
     * Odbijena vrednost
     */
    private Object rejectedValue;
    
    /**
     * Kreira field error
     */
    public static FieldError of(String field, String message) {
        return FieldError.builder()
                .field(field)
                .message(message)
                .build();
    }
    
    /**
     * Kreira field error sa odbijenom vrednošću
     */
    public static FieldError of(String field, String message, Object rejectedValue) {
        return FieldError.builder()
                .field(field)
                .message(message)
                .rejectedValue(rejectedValue)
                .build();
    }
}

// Made with Bob
