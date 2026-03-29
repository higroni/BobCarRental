package com.bobcarrental.service;

import com.bobcarrental.dto.request.AddressRequest;
import com.bobcarrental.dto.response.AddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for address operations
 */
public interface AddressService {

    /**
     * Get all addresses with pagination
     */
    Page<AddressResponse> getAllAddresses(Pageable pageable);

    /**
     * Get address by ID
     */
    AddressResponse getAddressById(Long id);

    /**
     * Get addresses by city
     */
    List<AddressResponse> getAddressesByCity(String city);

    /**
     * Get addresses by category
     */
    List<AddressResponse> getAddressesByCategory(String category);

    /**
     * Get active addresses
     */
    List<AddressResponse> getActiveAddresses();

    /**
     * Search addresses by name, company, or city
     */
    Page<AddressResponse> searchAddresses(String query, Pageable pageable);

    /**
     * Create new address
     */
    AddressResponse createAddress(AddressRequest request);

    /**
     * Update existing address
     */
    AddressResponse updateAddress(Long id, AddressRequest request);

    /**
     * Activate address
     */
    AddressResponse activateAddress(Long id);

    /**
     * Deactivate address
     */
    AddressResponse deactivateAddress(Long id);

    /**
     * Delete address
     */
    void deleteAddress(Long id);
}

// Made with Bob
