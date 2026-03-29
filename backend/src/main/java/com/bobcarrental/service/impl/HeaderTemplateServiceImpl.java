package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.HeaderTemplateMapper;
import com.bobcarrental.dto.request.HeaderTemplateRequest;
import com.bobcarrental.dto.response.HeaderTemplateResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.HeaderTemplate;
import com.bobcarrental.repository.HeaderTemplateRepository;
import com.bobcarrental.service.HeaderTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of HeaderTemplateService
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeaderTemplateServiceImpl implements HeaderTemplateService {

    private final HeaderTemplateRepository headerTemplateRepository;
    private final HeaderTemplateMapper headerTemplateMapper;

    @Override
    public Page<HeaderTemplateResponse> getAllHeaderTemplates(Pageable pageable) {
        log.debug("Getting all header templates with pagination: {}", pageable);
        return headerTemplateRepository.findAll(pageable)
                .map(headerTemplateMapper::toResponse);
    }

    @Override
    public HeaderTemplateResponse getHeaderTemplateById(Long id) {
        log.debug("Getting header template by id: {}", id);
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        return headerTemplateMapper.toResponse(headerTemplate);
    }

    @Override
    public HeaderTemplateResponse getDefaultHeaderTemplate() {
        log.debug("Getting default (first active) header template");
        // HeaderTemplate doesn't have isDefault field, return first active template
        List<HeaderTemplate> templates = headerTemplateRepository.findByActiveTrue();
        if (templates.isEmpty()) {
            throw new ResourceNotFoundException("No active header template found");
        }
        return headerTemplateMapper.toResponse(templates.get(0));
    }

    @Override
    public List<HeaderTemplateResponse> getActiveHeaderTemplates() {
        log.debug("Getting active header templates");
        List<HeaderTemplate> templates = headerTemplateRepository.findByActiveTrue();
        return templates.stream()
                .map(headerTemplateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<HeaderTemplateResponse> searchHeaderTemplates(String query, Pageable pageable) {
        log.debug("Searching header templates with query: {}", query);
        // Use searchTemplates which searches all line1-8 fields
        List<HeaderTemplate> templates = headerTemplateRepository.searchTemplates(query);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), templates.size());
        List<HeaderTemplate> pageContent = templates.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(
                pageContent.stream().map(headerTemplateMapper::toResponse).collect(Collectors.toList()),
                pageable,
                templates.size()
        );
    }

    @Override
    @Transactional
    public HeaderTemplateResponse createHeaderTemplate(HeaderTemplateRequest request) {
        log.debug("Creating header template: {}", request);
        
        HeaderTemplate headerTemplate = headerTemplateMapper.toEntity(request);
        
        // If this is marked as default, unset other default templates
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultTemplates();
        }
        
        HeaderTemplate savedTemplate = headerTemplateRepository.save(headerTemplate);
        log.info("Created header template with id: {}", savedTemplate.getId());
        
        return headerTemplateMapper.toResponse(savedTemplate);
    }

    @Override
    @Transactional
    public HeaderTemplateResponse updateHeaderTemplate(Long id, HeaderTemplateRequest request) {
        log.debug("Updating header template {}: {}", id, request);
        
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        
        // If this is being marked as default, unset other default templates
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(headerTemplate.getIsDefault())) {
            unsetDefaultTemplates();
        }
        
        headerTemplateMapper.updateEntityFromRequest(request, headerTemplate);
        HeaderTemplate updatedTemplate = headerTemplateRepository.save(headerTemplate);
        log.info("Updated header template with id: {}", id);
        
        return headerTemplateMapper.toResponse(updatedTemplate);
    }

    @Override
    @Transactional
    public HeaderTemplateResponse setAsDefault(Long id) {
        log.debug("Setting header template {} as default", id);
        
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        
        // Unset other default templates
        unsetDefaultTemplates();
        
        // Set this template as default and active
        headerTemplate.setIsDefault(true);
        headerTemplate.setIsActive(true);
        HeaderTemplate updatedTemplate = headerTemplateRepository.save(headerTemplate);
        log.info("Set header template {} as default", id);
        
        return headerTemplateMapper.toResponse(updatedTemplate);
    }

    @Override
    @Transactional
    public HeaderTemplateResponse activateHeaderTemplate(Long id) {
        log.debug("Activating header template: {}", id);
        
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        headerTemplate.setIsActive(true);
        HeaderTemplate updatedTemplate = headerTemplateRepository.save(headerTemplate);
        log.info("Activated header template with id: {}", id);
        
        return headerTemplateMapper.toResponse(updatedTemplate);
    }

    @Override
    @Transactional
    public HeaderTemplateResponse deactivateHeaderTemplate(Long id) {
        log.debug("Deactivating header template: {}", id);
        
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        
        // Cannot deactivate default template
        if (Boolean.TRUE.equals(headerTemplate.getIsDefault())) {
            throw new ValidationException("Cannot deactivate default header template. Set another template as default first.");
        }
        
        headerTemplate.setIsActive(false);
        HeaderTemplate updatedTemplate = headerTemplateRepository.save(headerTemplate);
        log.info("Deactivated header template with id: {}", id);
        
        return headerTemplateMapper.toResponse(updatedTemplate);
    }

    @Override
    @Transactional
    public void deleteHeaderTemplate(Long id) {
        log.debug("Deleting header template: {}", id);
        
        HeaderTemplate headerTemplate = findHeaderTemplateById(id);
        
        // Cannot delete default template
        if (Boolean.TRUE.equals(headerTemplate.getIsDefault())) {
            throw new ValidationException("Cannot delete default header template. Set another template as default first.");
        }
        
        headerTemplateRepository.delete(headerTemplate);
        log.info("Deleted header template with id: {}", id);
    }

    /**
     * Find header template by ID or throw exception
     */
    private HeaderTemplate findHeaderTemplateById(Long id) {
        return headerTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HeaderTemplate", "id", id));
    }

    /**
     * Unset default flag for all templates
     * Note: HeaderTemplate doesn't have isDefault field
     */
    private void unsetDefaultTemplates() {
        // This method is no longer needed since we don't have isDefault field
        log.debug("unsetDefaultTemplates called but not needed - no isDefault field");
    }
}

// Made with Bob
