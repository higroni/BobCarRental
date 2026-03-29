package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.AddressMapper;
import com.bobcarrental.dto.request.AddressRequest;
import com.bobcarrental.dto.response.AddressResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.model.Address;
import com.bobcarrental.repository.AddressRepository;
import com.bobcarrental.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AddressService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public Page<AddressResponse> getAllAddresses(Pageable pageable) {
        log.debug("Getting all addresses with pagination: {}", pageable);
        return addressRepository.findAll(pageable)
                .map(addressMapper::toResponse);
    }

    @Override
    public AddressResponse getAddressById(Long id) {
        log.debug("Getting address by id: {}", id);
        Address address = findAddressById(id);
        return addressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getAddressesByCity(String city) {
        log.debug("Getting addresses by city: {}", city);
        List<Address> addresses = addressRepository.findByCityIgnoreCase(city);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressResponse> getAddressesByCategory(String category) {
        log.debug("Getting addresses by department (category): {}", category);
        // Address entity doesn't have category field, using dept instead
        List<Address> addresses = addressRepository.findByDept(category);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressResponse> getActiveAddresses() {
        log.debug("Getting tagged addresses (no isActive field)");
        // Address entity doesn't have isActive field, using tagged instead
        List<Address> addresses = addressRepository.findByTaggedTrue();
        return addresses.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AddressResponse> searchAddresses(String query, Pageable pageable) {
        log.debug("Searching addresses with query: {}", query);
        // Use the searchAddresses method that searches clientId, dept, desc, city, phone
        return addressRepository.searchAddresses(query, pageable)
                .map(addressMapper::toResponse);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        log.debug("Creating address: {}", request);
        
        Address address = addressMapper.toEntity(request);
        Address savedAddress = addressRepository.save(address);
        log.info("Created address with id: {}", savedAddress.getId());
        
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        log.debug("Updating address {}: {}", id, request);
        
        Address address = findAddressById(id);
        addressMapper.updateEntityFromRequest(request, address);
        Address updatedAddress = addressRepository.save(address);
        log.info("Updated address with id: {}", id);
        
        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public AddressResponse activateAddress(Long id) {
        log.debug("Activating address: {}", id);
        
        Address address = findAddressById(id);
        address.setIsActive(true);
        Address updatedAddress = addressRepository.save(address);
        log.info("Activated address with id: {}", id);
        
        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public AddressResponse deactivateAddress(Long id) {
        log.debug("Deactivating address: {}", id);
        
        Address address = findAddressById(id);
        address.setIsActive(false);
        Address updatedAddress = addressRepository.save(address);
        log.info("Deactivated address with id: {}", id);
        
        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        log.debug("Deleting address: {}", id);
        
        Address address = findAddressById(id);
        addressRepository.delete(address);
        log.info("Deleted address with id: {}", id);
    }

    /**
     * Find address by ID or throw exception
     */
    private Address findAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }
}

// Made with Bob
