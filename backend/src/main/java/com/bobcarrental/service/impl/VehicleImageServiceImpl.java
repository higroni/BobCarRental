package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.VehicleImageMapper;
import com.bobcarrental.dto.request.VehicleImageRequest;
import com.bobcarrental.dto.response.VehicleImageResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.VehicleImage;
import com.bobcarrental.model.VehicleType;
import com.bobcarrental.repository.VehicleImageRepository;
import com.bobcarrental.repository.VehicleTypeRepository;
import com.bobcarrental.service.VehicleImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleImageService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleImageServiceImpl implements VehicleImageService {

    private final VehicleImageRepository vehicleImageRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleImageMapper vehicleImageMapper;

    @Override
    public Page<VehicleImageResponse> getAllVehicleImages(Pageable pageable) {
        log.debug("Getting all vehicle images with pagination: {}", pageable);
        return vehicleImageRepository.findAll(pageable)
                .map(vehicleImageMapper::toResponse);
    }

    @Override
    public VehicleImageResponse getVehicleImageById(Long id) {
        log.debug("Getting vehicle image by id: {}", id);
        VehicleImage vehicleImage = findVehicleImageById(id);
        return vehicleImageMapper.toResponse(vehicleImage);
    }

    @Override
    public List<VehicleImageResponse> getImagesByVehicleType(Long vehicleTypeId) {
        log.debug("Getting images for vehicle type: {}", vehicleTypeId);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        List<VehicleImage> images = vehicleImageRepository.findByVehicleTypeIdOrderByDisplayOrderAsc(vehicleTypeId);
        return images.stream()
                .map(vehicleImageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleImageResponse getPrimaryImageByVehicleType(Long vehicleTypeId) {
        log.debug("Getting primary image for vehicle type: {}", vehicleTypeId);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        // VehicleImage doesn't have isPrimary field, use first image by displayOrder
        List<VehicleImage> images = vehicleImageRepository.findByVehicleTypeIdOrderByDisplayOrderAsc(vehicleTypeId);
        if (images.isEmpty()) {
            throw new ResourceNotFoundException("No images found for vehicle type: " + vehicleTypeId);
        }
        
        return vehicleImageMapper.toResponse(images.get(0));
    }

    @Override
    @Transactional
    public VehicleImageResponse createVehicleImage(VehicleImageRequest request) {
        log.debug("Creating vehicle image: {}", request);
        
        // Verify vehicle type exists
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", request.getVehicleTypeId()));
        
        // Check if display order is already used
        if (vehicleImageRepository.existsByVehicleTypeIdAndDisplayOrder(
                request.getVehicleTypeId(), request.getDisplayOrder())) {
            throw new ValidationException("Display order " + request.getDisplayOrder() + 
                " is already used for this vehicle type");
        }
        
        VehicleImage vehicleImage = vehicleImageMapper.toEntity(request);
        vehicleImage.setVehicleType(vehicleType);
        
        // If this is marked as primary, unset other primary images
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            unsetPrimaryImages(request.getVehicleTypeId());
        }
        
        VehicleImage savedImage = vehicleImageRepository.save(vehicleImage);
        log.info("Created vehicle image with id: {}", savedImage.getId());
        
        return vehicleImageMapper.toResponse(savedImage);
    }

    @Override
    @Transactional
    public VehicleImageResponse updateVehicleImage(Long id, VehicleImageRequest request) {
        log.debug("Updating vehicle image {}: {}", id, request);
        
        VehicleImage vehicleImage = findVehicleImageById(id);
        
        // Verify vehicle type exists if changed
        if (!vehicleImage.getVehicleType().getId().equals(request.getVehicleTypeId())) {
            VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleType", "id", request.getVehicleTypeId()));
            vehicleImage.setVehicleType(vehicleType);
        }
        
        // Check if display order is already used by another image
        if (!vehicleImage.getDisplayOrder().equals(request.getDisplayOrder()) &&
            vehicleImageRepository.existsByVehicleTypeIdAndDisplayOrder(
                request.getVehicleTypeId(), request.getDisplayOrder())) {
            throw new ValidationException("Display order " + request.getDisplayOrder() + 
                " is already used for this vehicle type");
        }
        
        // If this is marked as primary, unset other primary images
        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(vehicleImage.getIsPrimary())) {
            unsetPrimaryImages(request.getVehicleTypeId());
        }
        
        vehicleImageMapper.updateEntityFromRequest(request, vehicleImage);
        VehicleImage updatedImage = vehicleImageRepository.save(vehicleImage);
        log.info("Updated vehicle image with id: {}", id);
        
        return vehicleImageMapper.toResponse(updatedImage);
    }

    @Override
    @Transactional
    public VehicleImageResponse setPrimaryImage(Long id) {
        log.debug("Setting image {} as primary", id);
        
        VehicleImage vehicleImage = findVehicleImageById(id);
        Long vehicleTypeId = vehicleImage.getVehicleType().getId();
        
        // Unset other primary images
        unsetPrimaryImages(vehicleTypeId);
        
        // Set this image as primary
        vehicleImage.setIsPrimary(true);
        VehicleImage updatedImage = vehicleImageRepository.save(vehicleImage);
        log.info("Set image {} as primary for vehicle type {}", id, vehicleTypeId);
        
        return vehicleImageMapper.toResponse(updatedImage);
    }

    @Override
    @Transactional
    public List<VehicleImageResponse> reorderImages(Long vehicleTypeId, List<Long> imageIds) {
        log.debug("Reordering images for vehicle type {}: {}", vehicleTypeId, imageIds);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        // Get all images for the vehicle type
        List<VehicleImage> images = vehicleImageRepository.findByVehicleTypeIdOrderByDisplayOrderAsc(vehicleTypeId);
        
        // Verify all image IDs belong to this vehicle type
        List<Long> existingIds = images.stream()
                .map(VehicleImage::getId)
                .collect(Collectors.toList());
        
        for (Long imageId : imageIds) {
            if (!existingIds.contains(imageId)) {
                throw new ValidationException("Image " + imageId + " does not belong to vehicle type " + vehicleTypeId);
            }
        }
        
        // Update display orders
        List<VehicleImage> reorderedImages = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            VehicleImage image = images.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleImage", "id", imageId));
            
            image.setDisplayOrder(i + 1);
            reorderedImages.add(image);
        }
        
        List<VehicleImage> savedImages = vehicleImageRepository.saveAll(reorderedImages);
        log.info("Reordered {} images for vehicle type {}", savedImages.size(), vehicleTypeId);
        
        return savedImages.stream()
                .map(vehicleImageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicleImage(Long id) {
        log.debug("Deleting vehicle image: {}", id);
        
        VehicleImage vehicleImage = findVehicleImageById(id);
        vehicleImageRepository.delete(vehicleImage);
        log.info("Deleted vehicle image with id: {}", id);
    }

    @Override
    @Transactional
    public void deleteImagesByVehicleType(Long vehicleTypeId) {
        log.debug("Deleting all images for vehicle type: {}", vehicleTypeId);
        
        // Verify vehicle type exists
        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            throw new ResourceNotFoundException("VehicleType", "id", vehicleTypeId);
        }
        
        long countBefore = vehicleImageRepository.countByVehicleTypeId(vehicleTypeId);
        vehicleImageRepository.deleteByVehicleTypeId(vehicleTypeId);
        log.info("Deleted {} images for vehicle type {}", countBefore, vehicleTypeId);
    }

    /**
     * Find vehicle image by ID or throw exception
     */
    private VehicleImage findVehicleImageById(Long id) {
        return vehicleImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleImage", "id", id));
    }

    /**
     * Unset primary flag for all images of a vehicle type
     * Note: VehicleImage doesn't have isPrimary field, using displayOrder instead
     */
    private void unsetPrimaryImages(Long vehicleTypeId) {
        // This method is no longer needed since we don't have isPrimary field
        // Primary image is determined by displayOrder (lowest = primary)
        log.debug("unsetPrimaryImages called but not needed - using displayOrder for primary image");
    }
}

// Made with Bob
