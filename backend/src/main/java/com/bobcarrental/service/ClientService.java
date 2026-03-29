package com.bobcarrental.service;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Client service interface.
 * Handles all business logic related to client management.
 */
public interface ClientService {
    
    /**
     * Create a new client
     */
    ClientResponse createClient(ClientRequest request);
    
    /**
     * Update an existing client
     */
    ClientResponse updateClient(Long id, ClientRequest request);
    
    /**
     * Get client by database ID
     */
    ClientResponse getClientById(Long id);
    
    /**
     * Get client by legacy client ID
     */
    ClientResponse getClientByClientId(String clientId);
    
    /**
     * Delete a client
     */
    void deleteClient(Long id);
    
    /**
     * Get all clients with pagination
     */
    Page<ClientSummaryResponse> getAllClients(Pageable pageable);
    
    /**
     * Search clients by name, ID, city, or phone
     */
    List<ClientSummaryResponse> searchClients(String search);
    
    /**
     * Get clients by city
     */
    List<ClientSummaryResponse> getClientsByCity(String city);
    
    /**
     * Validate client ID uniqueness (PresenceChk from legacy)
     */
    
    /**
     * Search clients by name
     */
    List<ClientSummaryResponse> searchClientsByName(String name);
    
    /**
     * Check if client name exists
     */
    boolean existsByName(String name);
    
    /**
     * Check if phone number exists
     */
    boolean existsByPhone(String phone);
    void validateClientId(String clientId, Long excludeId);
    
    /**
     * Count active clients
     */
    long countActiveClients();
}

// Made with Bob
