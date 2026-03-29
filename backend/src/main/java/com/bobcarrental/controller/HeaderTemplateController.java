package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.request.HeaderTemplateRequest;
import com.bobcarrental.dto.response.HeaderTemplateResponse;
import com.bobcarrental.service.HeaderTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for header template operations (Admin only)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/headertemplates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HeaderTemplateController {

    private final HeaderTemplateService headerTemplateService;

    /**
     * Get all header templates with pagination
     * GET /api/v1/headertemplates
     */
    @GetMapping
    public ResponseEntity<Page<HeaderTemplateResponse>> getAllHeaderTemplates(
            @PageableDefault(size = 20, sort = "templateName", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET /api/v1/headertemplates - Getting all header templates");
        Page<HeaderTemplateResponse> templates = headerTemplateService.getAllHeaderTemplates(pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get header template by ID
     * GET /api/v1/headertemplates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<HeaderTemplateResponse> getHeaderTemplateById(@PathVariable Long id) {
        log.info("GET /api/v1/headertemplates/{} - Getting header template by id", id);
        HeaderTemplateResponse template = headerTemplateService.getHeaderTemplateById(id);
        return ResponseEntity.ok(template);
    }

    /**
     * Get default header template
     * GET /api/v1/headertemplates/default
     */
    @GetMapping("/default")
    public ResponseEntity<HeaderTemplateResponse> getDefaultHeaderTemplate() {
        log.info("GET /api/v1/headertemplates/default - Getting default header template");
        HeaderTemplateResponse template = headerTemplateService.getDefaultHeaderTemplate();
        return ResponseEntity.ok(template);
    }

    /**
     * Get active header templates
     * GET /api/v1/headertemplates/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<HeaderTemplateResponse>> getActiveHeaderTemplates() {
        log.info("GET /api/v1/headertemplates/active - Getting active header templates");
        List<HeaderTemplateResponse> templates = headerTemplateService.getActiveHeaderTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Search header templates by name or company name
     * GET /api/v1/headertemplates/search?q={query}
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HeaderTemplateResponse>> searchHeaderTemplates(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "templateName") Pageable pageable) {
        log.info("GET /api/v1/headertemplates/search?q={} - Searching header templates", q);
        Page<HeaderTemplateResponse> templates = headerTemplateService.searchHeaderTemplates(q, pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * Create new header template
     * POST /api/v1/headertemplates
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HeaderTemplateResponse>> createHeaderTemplate(
            @Valid @RequestBody HeaderTemplateRequest request) {
        log.info("POST /api/v1/headertemplates - Creating header template");
        HeaderTemplateResponse createdTemplate = headerTemplateService.createHeaderTemplate(request);
        
        ApiResponse<HeaderTemplateResponse> response = ApiResponse.<HeaderTemplateResponse>builder()
                .success(true)
                .message("Header template created successfully")
                .data(createdTemplate)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing header template
     * PUT /api/v1/headertemplates/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HeaderTemplateResponse>> updateHeaderTemplate(
            @PathVariable Long id,
            @Valid @RequestBody HeaderTemplateRequest request) {
        log.info("PUT /api/v1/headertemplates/{} - Updating header template", id);
        HeaderTemplateResponse updatedTemplate = headerTemplateService.updateHeaderTemplate(id, request);
        
        ApiResponse<HeaderTemplateResponse> response = ApiResponse.<HeaderTemplateResponse>builder()
                .success(true)
                .message("Header template updated successfully")
                .data(updatedTemplate)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Set header template as default
     * PATCH /api/v1/headertemplates/{id}/set-default
     */
    @PatchMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<HeaderTemplateResponse>> setAsDefault(@PathVariable Long id) {
        log.info("PATCH /api/v1/headertemplates/{}/set-default - Setting as default", id);
        HeaderTemplateResponse updatedTemplate = headerTemplateService.setAsDefault(id);
        
        ApiResponse<HeaderTemplateResponse> response = ApiResponse.<HeaderTemplateResponse>builder()
                .success(true)
                .message("Header template set as default successfully")
                .data(updatedTemplate)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Activate header template
     * PATCH /api/v1/headertemplates/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<HeaderTemplateResponse>> activateHeaderTemplate(@PathVariable Long id) {
        log.info("PATCH /api/v1/headertemplates/{}/activate - Activating header template", id);
        HeaderTemplateResponse updatedTemplate = headerTemplateService.activateHeaderTemplate(id);
        
        ApiResponse<HeaderTemplateResponse> response = ApiResponse.<HeaderTemplateResponse>builder()
                .success(true)
                .message("Header template activated successfully")
                .data(updatedTemplate)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate header template
     * PATCH /api/v1/headertemplates/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<HeaderTemplateResponse>> deactivateHeaderTemplate(@PathVariable Long id) {
        log.info("PATCH /api/v1/headertemplates/{}/deactivate - Deactivating header template", id);
        HeaderTemplateResponse updatedTemplate = headerTemplateService.deactivateHeaderTemplate(id);
        
        ApiResponse<HeaderTemplateResponse> response = ApiResponse.<HeaderTemplateResponse>builder()
                .success(true)
                .message("Header template deactivated successfully")
                .data(updatedTemplate)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete header template
     * DELETE /api/v1/headertemplates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHeaderTemplate(@PathVariable Long id) {
        log.info("DELETE /api/v1/headertemplates/{} - Deleting header template", id);
        headerTemplateService.deleteHeaderTemplate(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Header template deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}

// Made with Bob
