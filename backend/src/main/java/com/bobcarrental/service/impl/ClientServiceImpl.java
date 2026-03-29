package com.bobcarrental.service.impl;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.mapper.ClientMapper;
import com.bobcarrental.model.Client;
import com.bobcarrental.repository.ClientRepository;
import com.bobcarrental.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Client service implementation.
 * Implements business logic for client management with legacy validation rules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.debug("Creating new client with clientId: {}", request.getClientId());
        
        // Validation: PresenceChk - Check if clientId already exists
        validateClientId(request.getClientId(), null);
        
        // Map and save
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);
        
        log.info("Client created successfully with id: {} and clientId: {}", 
                savedClient.getId(), savedClient.getClientId());
        return clientMapper.toResponse(savedClient);
    }
    
    @Override
    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        log.debug("Updating client with id: {}", id);
        
        // Find existing client
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        
        // Validation: Check if new clientId is unique (if changed)
        if (!existingClient.getClientId().equals(request.getClientId())) {
            validateClientId(request.getClientId(), id);
        }
        
        // Update and save
        clientMapper.updateEntityFromRequest(request, existingClient);
        Client updatedClient = clientRepository.save(existingClient);
        
        log.info("Client updated successfully with id: {}", id);
        return clientMapper.toResponse(updatedClient);
    }
    
    @Override
    public ClientResponse getClientById(Long id) {
        log.debug("Fetching client by id: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        
        return clientMapper.toResponse(client);
    }
    
    @Override
    public ClientResponse getClientByClientId(String clientId) {
        log.debug("Fetching client by clientId: {}", clientId);
        
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));
        
        return clientMapper.toResponse(client);
    }
    
    @Override
    @Transactional
    public void deleteClient(Long id) {
        log.debug("Deleting client with id: {}", id);
        
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", "id", id);
        }
        
        // Note: In legacy system, clients with bookings cannot be deleted
        // Consider adding check for existing bookings before deletion
        clientRepository.deleteById(id);
        
        log.info("Client deleted successfully with id: {}", id);
    }
    
    @Override
    public Page<ClientSummaryResponse> getAllClients(Pageable pageable) {
        log.debug("Fetching all clients with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Client> clientPage = clientRepository.findAll(pageable);
        return clientPage.map(clientMapper::toSummaryResponse);
    }
    
    @Override
    public List<ClientSummaryResponse> searchClients(String search) {
        log.debug("Searching clients with query: {}", search);
        
        List<Client> clients = clientRepository.searchClients(search);
        return clientMapper.toSummaryResponseList(clients);
    }
    
    @Override
    public List<ClientSummaryResponse> getClientsByCity(String city) {
        log.debug("Fetching clients by city: {}", city);
        
        List<Client> clients = clientRepository.findByCity(city);
        return clientMapper.toSummaryResponseList(clients);
    }
    
    @Override
    public void validateClientId(String clientId, Long excludeId) {
        // PresenceChk validation from legacy system
        boolean exists = clientRepository.existsByClientId(clientId);
        
        if (exists) {
            // If updating, check if it's the same client
            if (excludeId != null) {
                Client existingClient = clientRepository.findByClientId(clientId).orElse(null);
                if (existingClient != null && !existingClient.getId().equals(excludeId)) {
                    throw new ValidationException("Client ID already exists: " + clientId);
                }
            } else {
                throw new ValidationException("Client ID already exists: " + clientId);
            }
        }
    }
    
    @Override
    public long countActiveClients() {
        long count = clientRepository.countActiveClients();
        log.debug("Active clients count: {}", count);
        return count;
    }
    
    @Override
    public List<ClientSummaryResponse> searchClientsByName(String name) {
        log.info("Searching clients by name: {}", name);
        List<Client> clients = clientRepository.searchClientsByName(name);
        return clientMapper.toSummaryResponseList(clients);
    }
    
    @Override
    public boolean existsByName(String name) {
        return clientRepository.existsByClientName(name);
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return clientRepository.existsByPhone(phone);
    }
}

// Made with Bob
