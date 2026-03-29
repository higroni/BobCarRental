package com.bobcarrental.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper za paginirane odgovore
 * Koristi se za liste sa paginacijom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /**
     * Lista elemenata na trenutnoj stranici
     */
    private List<T> content;
    
    /**
     * Broj trenutne stranice (0-based)
     */
    private int page;
    
    /**
     * Broj elemenata po stranici
     */
    private int size;
    
    /**
     * Ukupan broj elemenata
     */
    private long totalElements;
    
    /**
     * Ukupan broj stranica
     */
    private int totalPages;
    
    /**
     * Da li je ovo prva stranica
     */
    private boolean first;
    
    /**
     * Da li je ovo poslednja stranica
     */
    private boolean last;
    
    /**
     * Da li ima prethodnu stranicu
     */
    private boolean hasPrevious;
    
    /**
     * Da li ima sledeću stranicu
     */
    private boolean hasNext;
    
    /**
     * Kreira PageResponse iz Spring Data Page objekta
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .build();
    }
    
    /**
     * Kreira PageResponse sa mapiranim sadržajem
     */
    public static <T, R> PageResponse<R> of(Page<T> page, List<R> mappedContent) {
        return PageResponse.<R>builder()
                .content(mappedContent)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .build();
    }
}

// Made with Bob
