package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.VehicleTypeMapper;
import com.bobcarrental.dto.request.VehicleTypeRequest;
import com.bobcarrental.dto.response.VehicleTypeResponse;
import com.bobcarrental.dto.response.VehicleTypeSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.VehicleTypeRepository;
import com.bobcarrental.service.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleTypeService
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleTypeServiceImpl implements VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleTypeMapper vehicleTypeMapper;

    @Override
    public Page<VehicleTypeSummaryResponse> getAllVehicleTypes(Pageable pageable) {
        log.debug("Fetching all vehicle types with pagination");
        return vehicleTypeRepository.findAll(pageable)
                .map(vehicleTypeMapper::toSummaryResponse);
    }

    @Override
    public List<VehicleTypeSummaryResponse> getActiveVehicleTypes() {
        log.debug("Fetching all active vehicle types");
        return vehicleTypeRepository.findByActiveTrue()
                .stream()
                .map(vehicleTypeMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleTypeResponse getVehicleTypeById(Long id) {
        log.debug("Fetching vehicle type with id: {}", id);
        VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));
        return vehicleTypeMapper.toResponse(vehicleType);
    }

    @Override
    public List<VehicleTypeSummaryResponse> searchVehicleTypesByName(String typeName) {
        log.debug("Searching vehicle types with description containing: {}", typeName);
        return vehicleTypeRepository.findByTypeDescContainingIgnoreCase(typeName)
                .stream()
                .map(vehicleTypeMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleTypeSummaryResponse> getVehicleTypesByCapacity(Integer capacity) {
        log.debug("Fetching vehicle types with capacity: {} (field not available, returning all)", capacity);
        // VehicleType doesn't have seatingCapacity field, return all types
        return vehicleTypeRepository.findAll()
                .stream()
                .map(vehicleTypeMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleTypeSummaryResponse> getVehicleTypesByAc(Boolean hasAc) {
        log.debug("Fetching vehicle types with AC: {} (field not available, returning all)", hasAc);
        // VehicleType doesn't have hasAc field, return all types
        return vehicleTypeRepository.findAll()
                .stream()
                .map(vehicleTypeMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleTypeResponse createVehicleType(VehicleTypeRequest request) {
        log.info("Creating new vehicle type: {}", request.getTypeName());

        // Validate unique type description
        if (vehicleTypeRepository.existsByTypeDesc(request.getTypeName())) {
            throw new ValidationException("Vehicle type description already exists: " + request.getTypeName());
        }

        VehicleType vehicleType = vehicleTypeMapper.toEntity(request);
        VehicleType savedVehicleType = vehicleTypeRepository.save(vehicleType);

        log.info("Vehicle type created successfully with id: {}", savedVehicleType.getId());
        return vehicleTypeMapper.toResponse(savedVehicleType);
    }

    @Override
    @Transactional
    public VehicleTypeResponse updateVehicleType(Long id, VehicleTypeRequest request) {
        log.info("Updating vehicle type with id: {}", id);

        VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));

        // Validate unique type description (excluding current record)
        if (vehicleTypeRepository.existsByTypeDescAndIdNot(request.getTypeName(), id)) {
            throw new ValidationException("Vehicle type description already exists: " + request.getTypeName());
        }

        vehicleTypeMapper.updateEntityFromRequest(request, vehicleType);
        VehicleType updatedVehicleType = vehicleTypeRepository.save(vehicleType);

        log.info("Vehicle type updated successfully with id: {}", id);
        return vehicleTypeMapper.toResponse(updatedVehicleType);
    }

    @Override
    @Transactional
    public void deleteVehicleType(Long id) {
        log.info("Deleting vehicle type with id: {}", id);

        VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", id));

        // Check if vehicle type has associated bookings
        if (vehicleType.getBookings() != null && !vehicleType.getBookings().isEmpty()) {
            throw new ValidationException("Cannot delete vehicle type with existing bookings");
        }

        vehicleTypeRepository.delete(vehicleType);
        log.info("Vehicle type deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsByTypeName(String typeName) {
        return vehicleTypeRepository.existsByTypeDesc(typeName);
    }

    @Override
    public boolean existsByTypeNameAndIdNot(String typeName, Long excludeId) {
        return vehicleTypeRepository.existsByTypeDescAndIdNot(typeName, excludeId);
    }
}

// Made with Bob
