package com.bobcarrental.controller;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.service.ClientService;
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
 * REST Controller for Client management
 * Handles CRUD operations for clients
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * Get all clients with pagination
     * 
     * @param pageable Pagination parameters
     * @return Paginated list of clients
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PageResponse<ClientSummaryResponse>>> getAllClients(
            @PageableDefault(size = 20, sort = "clientName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("Fetching all clients with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ClientSummaryResponse> clientPage = clientService.getAllClients(pageable);
        PageResponse<ClientSummaryResponse> pageResponse = PageResponse.of(clientPage);
        
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved successfully", pageResponse));
    }

    /**
     * Get client by ID
     * 
     * @param id Client ID
     * @return Client details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable Long id) {
        log.info("Fetching client with id: {}", id);
        
        ClientResponse client = clientService.getClientById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Client retrieved successfully", client));
    }

    /**
     * Search clients by name
     * 
     * @param name Search term
     * @return List of matching clients
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<ClientSummaryResponse>>> searchClients(
            @RequestParam String name) {
        
        log.info("Searching clients with name containing: {}", name);
        
        List<ClientSummaryResponse> clients = clientService.searchClientsByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d clients matching '%s'", clients.size(), name), clients));
    }

    /**
     * Get clients by city
     * 
     * @param city City name
     * @return List of clients in the city
     */
    @GetMapping("/by-city")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<ClientSummaryResponse>>> getClientsByCity(
            @RequestParam String city) {
        
        log.info("Fetching clients from city: {}", city);
        
        List<ClientSummaryResponse> clients = clientService.getClientsByCity(city);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d clients in %s", clients.size(), city), clients));
    }

    /**
     * Create new client
     * 
     * @param request Client creation request
     * @return Created client
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @Valid @RequestBody ClientRequest request) {
        
        log.info("Creating new client: {}", request.getName());
        
        ClientResponse client = clientService.createClient(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client created successfully", client));
    }

    /**
     * Update existing client
     * 
     * @param id Client ID
     * @param request Client update request
     * @return Updated client
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        
        log.info("Updating client with id: {}", id);
        
        ClientResponse client = clientService.updateClient(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Client updated successfully", client));
    }

    /**
     * Delete client
     * 
     * @param id Client ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        log.info("Deleting client with id: {}", id);
        
        clientService.deleteClient(id);
        
        return ResponseEntity.ok(ApiResponse.success("Client deleted successfully", null));
    }

    /**
     * Check if client name exists
     * 
     * @param name Client name
     * @return Validation result
     */
    @GetMapping("/validate/name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateClientName(@RequestParam String name) {
        log.info("Validating client name: {}", name);
        
        boolean exists = clientService.existsByName(name);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Client name already exists" : "Client name is available", !exists));
    }

    /**
     * Check if phone number exists
     * 
     * @param phone Phone number
     * @return Validation result
     */
    @GetMapping("/validate/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validatePhone(@RequestParam String phone) {
        log.info("Validating phone number: {}", phone);
        
        boolean exists = clientService.existsByPhone(phone);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Phone number already exists" : "Phone number is available", !exists));
    }
}

// Made with Bob
