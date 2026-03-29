package com.bobcarrental.controller;

import com.bobcarrental.dto.request.BillingRequest;
import com.bobcarrental.dto.response.BillingResponse;
import com.bobcarrental.dto.response.BillingSummaryResponse;
import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Billing management
 * Handles CRUD operations for billing records
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/billings")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    /**
     * Get all billings with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PageResponse<BillingSummaryResponse>>> getAllBillings(
            @PageableDefault(size = 20, sort = "billDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Fetching all billings with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<BillingSummaryResponse> billingPage = billingService.getAllBillings(pageable);
        PageResponse<BillingSummaryResponse> pageResponse = PageResponse.of(billingPage);
        
        return ResponseEntity.ok(ApiResponse.success("Billings retrieved successfully", pageResponse));
    }

    /**
     * Get billing by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BillingResponse>> getBillingById(@PathVariable Long id) {
        log.info("Fetching billing with id: {}", id);
        
        BillingResponse billing = billingService.getBillingById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Billing retrieved successfully", billing));
    }

    /**
     * Get billing by bill number
     */
    @GetMapping("/by-number/{billNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<BillingResponse>> getBillingByBillNo(@PathVariable String billNo) {
        log.info("Fetching billing with bill number: {}", billNo);
        
        BillingResponse billing = billingService.getBillingByBillNo(billNo);
        
        return ResponseEntity.ok(ApiResponse.success("Billing retrieved successfully", billing));
    }

    /**
     * Get billings by trip sheet ID
     */
    @GetMapping("/by-tripsheet/{tripSheetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BillingSummaryResponse>>> getBillingsByTripSheet(
            @PathVariable Long tripSheetId) {
        
        log.info("Fetching billings for trip sheet id: {}", tripSheetId);
        
        List<BillingSummaryResponse> billings = billingService.getBillingsByTripSheet(tripSheetId);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d billings for trip sheet", billings.size()), billings));
    }

    /**
     * Get billings by client ID
     */
    @GetMapping("/by-client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BillingSummaryResponse>>> getBillingsByClient(
            @PathVariable Long clientId) {
        
        log.info("Fetching billings for client id: {}", clientId);
        
        List<BillingSummaryResponse> billings = billingService.getBillingsByClient(clientId);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d billings for client", billings.size()), billings));
    }

    /**
     * Get billings by date range
     */
    @GetMapping("/by-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<BillingSummaryResponse>>> getBillingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching billings between {} and {}", startDate, endDate);
        
        List<BillingSummaryResponse> billings = billingService.getBillingsByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d billings in date range", billings.size()), billings));
    }

    /**
     * Get unpaid billings
     */
    @GetMapping("/unpaid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BillingSummaryResponse>>> getUnpaidBillings() {
        log.info("Fetching unpaid billings");
        
        List<BillingSummaryResponse> billings = billingService.getUnpaidBillings();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d unpaid billings", billings.size()), billings));
    }
    /**
     * Generate bill content preview for a client
     * Used to show bill preview when client is selected in the form
     */
    @GetMapping("/preview/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<String>> generateBillPreview(
            @PathVariable String clientId) {
        
        log.info("Generating bill preview for client: {}", clientId);
        
        String billPreview = billingService.generateBillPreview(clientId);
        
        return ResponseEntity.ok(ApiResponse.success("Bill preview generated", billPreview));
    }


    /**
     * Create new billing
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingResponse>> createBilling(
            @Valid @RequestBody BillingRequest request) {
        
        log.info("Creating new billing: {}", request.getBillNum());
        
        BillingResponse billing = billingService.createBilling(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Billing created successfully", billing));
    }

    /**
     * Update existing billing
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingResponse>> updateBilling(
            @PathVariable Long id,
            @Valid @RequestBody BillingRequest request) {
        
        log.info("Updating billing with id: {}", id);
        
        BillingResponse billing = billingService.updateBilling(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Billing updated successfully", billing));
    }

    /**
     * Record payment for a billing
     */
    @PatchMapping("/{id}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingResponse>> recordPayment(
            @PathVariable Long id,
            @RequestParam Double amount) {
        
        log.info("Recording payment of {} for billing id: {}", amount, id);
        
        BillingResponse billing = billingService.recordPayment(id, amount);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Payment of %.2f recorded successfully", amount), billing));
    }

    /**
     * Calculate GST
     */
    @GetMapping("/calculate-gst")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Double[]>> calculateGst(
            @RequestParam Double amount,
            @RequestParam Double gstRate,
            @RequestParam boolean isInterState) {
        
        log.info("Calculating GST for amount: {}, rate: {}, inter-state: {}", 
                amount, gstRate, isInterState);
        
        Double[] gst = billingService.calculateGst(amount, gstRate, isInterState);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("GST calculated: CGST=%.2f, SGST=%.2f, IGST=%.2f", gst[0], gst[1], gst[2]), gst));
    }

    /**
     * Delete billing
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBilling(@PathVariable Long id) {
        log.info("Deleting billing with id: {}", id);
        
        billingService.deleteBilling(id);
        
        return ResponseEntity.ok(ApiResponse.success("Billing deleted successfully", null));
    }

    /**
     * Check if bill number exists
     */
    @GetMapping("/validate/bill-no")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateBillNo(@RequestParam String billNo) {
        log.info("Validating bill number: {}", billNo);
        
        boolean exists = billingService.existsByBillNo(billNo);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Bill number already exists" : "Bill number is available",
                !exists));
    }
}

// Made with Bob
