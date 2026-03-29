package com.bobcarrental.controller;

import com.bobcarrental.dto.request.AddressRequest;
import com.bobcarrental.dto.response.AddressResponse;
import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.service.AddressService;
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
 * REST controller for address operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Get all addresses with pagination
     * GET /api/v1/addresses
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AddressResponse>>> getAllAddresses(
            @PageableDefault(size = 20, sort = "clientId", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET /api/v1/addresses - Getting all addresses");
        Page<AddressResponse> addressPage = addressService.getAllAddresses(pageable);
        PageResponse<AddressResponse> pageResponse = PageResponse.of(addressPage);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", pageResponse));
    }

    /**
     * Get address by ID
     * GET /api/v1/addresses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(@PathVariable Long id) {
        log.info("GET /api/v1/addresses/{} - Getting address by id", id);
        AddressResponse address = addressService.getAddressById(id);
        return ResponseEntity.ok(ApiResponse.success("Address retrieved successfully", address));
    }

    /**
     * Get addresses by city
     * GET /api/v1/addresses/city/{city}
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<AddressResponse>> getAddressesByCity(@PathVariable String city) {
        log.info("GET /api/v1/addresses/city/{} - Getting addresses by city", city);
        List<AddressResponse> addresses = addressService.getAddressesByCity(city);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get addresses by category
     * GET /api/v1/addresses/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<AddressResponse>> getAddressesByCategory(@PathVariable String category) {
        log.info("GET /api/v1/addresses/category/{} - Getting addresses by category", category);
        List<AddressResponse> addresses = addressService.getAddressesByCategory(category);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get active addresses
     * GET /api/v1/addresses/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<AddressResponse>> getActiveAddresses() {
        log.info("GET /api/v1/addresses/active - Getting active addresses");
        List<AddressResponse> addresses = addressService.getActiveAddresses();
        return ResponseEntity.ok(addresses);
    }

    /**
     * Search addresses by name, company, or city
     * GET /api/v1/addresses/search?q={query}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<AddressResponse>>> searchAddresses(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "clientId") Pageable pageable) {
        log.info("GET /api/v1/addresses/search?q={} - Searching addresses", q);
        Page<AddressResponse> addressPage = addressService.searchAddresses(q, pageable);
        PageResponse<AddressResponse> pageResponse = PageResponse.of(addressPage);
        return ResponseEntity.ok(ApiResponse.success("Addresses found", pageResponse));
    }

    /**
     * Create new address
     * POST /api/v1/addresses
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request) {
        log.info("POST /api/v1/addresses - Creating address");
        AddressResponse createdAddress = addressService.createAddress(request);
        
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address created successfully")
                .data(createdAddress)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing address
     * PUT /api/v1/addresses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        log.info("PUT /api/v1/addresses/{} - Updating address", id);
        AddressResponse updatedAddress = addressService.updateAddress(id, request);
        
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address updated successfully")
                .data(updatedAddress)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Activate address
     * PATCH /api/v1/addresses/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> activateAddress(@PathVariable Long id) {
        log.info("PATCH /api/v1/addresses/{}/activate - Activating address", id);
        AddressResponse updatedAddress = addressService.activateAddress(id);
        
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address activated successfully")
                .data(updatedAddress)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate address
     * PATCH /api/v1/addresses/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AddressResponse>> deactivateAddress(@PathVariable Long id) {
        log.info("PATCH /api/v1/addresses/{}/deactivate - Deactivating address", id);
        AddressResponse updatedAddress = addressService.deactivateAddress(id);
        
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address deactivated successfully")
                .data(updatedAddress)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete address
     * DELETE /api/v1/addresses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /api/v1/addresses/{} - Deleting address", id);
        addressService.deleteAddress(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Address deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}

// Made with Bob
