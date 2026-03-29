package com.bobcarrental.service;

import com.bobcarrental.dto.request.HeaderTemplateRequest;
import com.bobcarrental.dto.response.HeaderTemplateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for header template operations
 */
public interface HeaderTemplateService {

    /**
     * Get all header templates with pagination
     */
    Page<HeaderTemplateResponse> getAllHeaderTemplates(Pageable pageable);

    /**
     * Get header template by ID
     */
    HeaderTemplateResponse getHeaderTemplateById(Long id);

    /**
     * Get default header template
     */
    HeaderTemplateResponse getDefaultHeaderTemplate();

    /**
     * Get active header templates
     */
    List<HeaderTemplateResponse> getActiveHeaderTemplates();

    /**
     * Search header templates by name or company name
     */
    Page<HeaderTemplateResponse> searchHeaderTemplates(String query, Pageable pageable);

    /**
     * Create new header template
     */
    HeaderTemplateResponse createHeaderTemplate(HeaderTemplateRequest request);

    /**
     * Update existing header template
     */
    HeaderTemplateResponse updateHeaderTemplate(Long id, HeaderTemplateRequest request);

    /**
     * Set header template as default
     */
    HeaderTemplateResponse setAsDefault(Long id);

    /**
     * Activate header template
     */
    HeaderTemplateResponse activateHeaderTemplate(Long id);

    /**
     * Deactivate header template
     */
    HeaderTemplateResponse deactivateHeaderTemplate(Long id);

    /**
     * Delete header template
     */
    void deleteHeaderTemplate(Long id);
}

// Made with Bob
