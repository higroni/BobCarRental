package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.StandardFareMapper;
import com.bobcarrental.dto.request.StandardFareRequest;
import com.bobcarrental.dto.response.StandardFareResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.StandardFare;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.StandardFareRepository;
import com.bobcarrental.repository.VehicleTypeRepository;
import com.bobcarrental.service.StandardFareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StandardFareService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandardFareServiceImpl implements StandardFareService {

    private final StandardFareRepository standardFareRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final StandardFareMapper standardFareMapper;

    @Override
    public Page<StandardFareResponse> getAllStandardFares(Pageable pageable) {
        log.debug("Getting all standard fares with pagination: {}", pageable);
        return standardFareRepository.findAll(pageable)
                .map(standardFareMapper::toResponse);
    }

    @Override
    public StandardFareResponse getStandardFareById(Long id) {
        log.debug("Getting standard fare by id: {}", id);
        StandardFare standardFare = findStandardFareById(id);
        return standardFareMapper.toResponse(standardFare);
    }

    @Override
    public List<StandardFareResponse> getStandardFaresByVehicleType(Long vehicleTypeId) {
        log.debug("Getting standard fares for vehicle type: {}", vehicleTypeId);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        List<StandardFare> fares = standardFareRepository.findByVehicleTypeId(vehicleTypeId);
        return fares.stream()
                .map(standardFareMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StandardFareResponse getStandardFareByVehicleTypeAndFareType(Long vehicleTypeId, String fareType) {
        log.debug("Getting standard fare for vehicle type {} and fare type {}", vehicleTypeId, fareType);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        // Validate fare type
        if (!isValidFareType(fareType)) {
            throw new ValidationException("Invalid fare type: " + fareType);
        }
        
        return standardFareRepository.findByVehicleTypeIdAndFareType(vehicleTypeId, fareType)
                .map(standardFareMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "StandardFare not found for vehicle type " + vehicleTypeId + " and fare type " + fareType
                ));
    }

    @Override
    public List<StandardFareResponse> getStandardFaresByFareType(String fareType) {
        log.debug("Getting standard fares by fare type: {}", fareType);
        
        // Validate fare type
        if (!isValidFareType(fareType)) {
            throw new ValidationException("Invalid fare type: " + fareType);
        }
        
        List<StandardFare> fares = standardFareRepository.findByFareType(StandardFare.FareType.valueOf(fareType));
        return fares.stream()
                .map(standardFareMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StandardFareResponse> getActiveStandardFares() {
        log.debug("Getting active standard fares");
        List<StandardFare> fares = standardFareRepository.findByIsActiveTrue();
        return fares.stream()
                .map(standardFareMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StandardFareResponse createStandardFare(StandardFareRequest request) {
        log.debug("Creating standard fare: {}", request);
        
        // Verify vehicle type exists
        VehicleType vehicleType = vehicleTypeRepository.findByTypeId(request.getVehicleCode())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "typeId", request.getVehicleCode()));
        
        // Validate fare type
        if (!isValidFareType(request.getFareType())) {
            throw new ValidationException("Invalid fare type: " + request.getFareType());
        }
        
        // Check if standard fare already exists for this vehicle type and fare type
        if (standardFareRepository.existsByVehicleTypeTypeIdAndFareType(
                request.getVehicleCode(), request.getFareType())) {
            throw new ValidationException("Standard fare already exists for vehicle type " +
                vehicleType.getName() + " and fare type " + request.getFareType());
        }
        
        StandardFare standardFare = standardFareMapper.toEntity(request);
        standardFare.setVehicleType(vehicleType);
        
        StandardFare savedFare = standardFareRepository.save(standardFare);
        log.info("Created standard fare with id: {}", savedFare.getId());
        
        return standardFareMapper.toResponse(savedFare);
    }

    @Override
    @Transactional
    public StandardFareResponse updateStandardFare(Long id, StandardFareRequest request) {
        log.debug("Updating standard fare {}: {}", id, request);
        
        StandardFare standardFare = findStandardFareById(id);
        
        // Verify vehicle type exists and update if changed
        if (!standardFare.getVehicleCode().equals(request.getVehicleCode())) {
            VehicleType vehicleType = vehicleTypeRepository.findByTypeId(request.getVehicleCode())
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "typeId", request.getVehicleCode()));
            standardFare.setVehicleType(vehicleType);
        }
        
        // Validate fare type
        if (!isValidFareType(request.getFareType())) {
            throw new ValidationException("Invalid fare type: " + request.getFareType());
        }
        
        // Check if changing to a combination that already exists (excluding current record)
        if (!standardFare.getVehicleCode().equals(request.getVehicleCode()) ||
            !standardFare.getFareType().name().equals(request.getFareType())) {
            if (standardFareRepository.existsByVehicleTypeTypeIdAndFareType(
                    request.getVehicleCode(), request.getFareType())) {
                throw new ValidationException("Standard fare already exists for this vehicle type and fare type combination");
            }
        }
        
        // Update entity from request
        standardFareMapper.updateEntityFromRequest(request, standardFare);
        StandardFare updatedFare = standardFareRepository.save(standardFare);
        log.info("Updated standard fare with id: {}", id);
        
        return standardFareMapper.toResponse(updatedFare);
    }

    @Override
    @Transactional
    public StandardFareResponse activateStandardFare(Long id) {
        log.debug("Activating standard fare: {}", id);
        
        StandardFare standardFare = findStandardFareById(id);
        standardFare.setIsActive(true);
        StandardFare updatedFare = standardFareRepository.save(standardFare);
        log.info("Activated standard fare with id: {}", id);
        
        return standardFareMapper.toResponse(updatedFare);
    }

    @Override
    @Transactional
    public StandardFareResponse deactivateStandardFare(Long id) {
        log.debug("Deactivating standard fare: {}", id);
        
        StandardFare standardFare = findStandardFareById(id);
        standardFare.setIsActive(false);
        StandardFare updatedFare = standardFareRepository.save(standardFare);
        log.info("Deactivated standard fare with id: {}", id);
        
        return standardFareMapper.toResponse(updatedFare);
    }

    @Override
    @Transactional
    public void deleteStandardFare(Long id) {
        log.debug("Deleting standard fare: {}", id);
        
        StandardFare standardFare = findStandardFareById(id);
        standardFareRepository.delete(standardFare);
        log.info("Deleted standard fare with id: {}", id);
    }

    @Override
    public boolean existsByVehicleTypeAndFareType(Long vehicleTypeId, String fareType) {
        return standardFareRepository.existsByVehicleTypeIdAndFareType(vehicleTypeId, fareType);
    }

    /**
     * Find standard fare by ID or throw exception
     */
    private StandardFare findStandardFareById(Long id) {
        return standardFareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StandardFare", "id", id));
    }

    /**
     * Validate fare type
     */
    private boolean isValidFareType(String fareType) {
        return fareType != null && (
            fareType.equals("LOCAL") ||
            fareType.equals("EXTRA") ||
            fareType.equals("GENERAL") ||
            fareType.equals("OUTSTATION")
        );
    }
}

// Made with Bob
