package com.bobcarrental.service;

import com.bobcarrental.dto.request.BillingRequest;
import com.bobcarrental.dto.response.BillingResponse;
import com.bobcarrental.dto.response.BillingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Billing operations
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
public interface BillingService {

    /**
     * Create a new billing record
     * 
     * @param request Billing creation request
     * @return Created billing
     */
    BillingResponse createBilling(BillingRequest request);

    /**
     * Update an existing billing record
     * 
     * @param id Billing ID
     * @param request Billing update request
     * @return Updated billing
     */
    BillingResponse updateBilling(Long id, BillingRequest request);

    /**
     * Get billing by ID
     * 
     * @param id Billing ID
     * @return Billing details
     */
    BillingResponse getBillingById(Long id);

    /**
     * Get billing by bill number
     * 
     * @param billNo Bill number
     * @return Billing details
     */
    BillingResponse getBillingByBillNo(String billNo);

    /**
     * Get all billings with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of billings
     */
    Page<BillingSummaryResponse> getAllBillings(Pageable pageable);

    /**
     * Get billings by trip sheet ID
     * 
     * @param tripSheetId Trip sheet ID
     * @return List of billings
     */
    List<BillingSummaryResponse> getBillingsByTripSheet(Long tripSheetId);

    /**
     * Get billings by client ID
     * 
     * @param clientId Client ID
     * @return List of billings
     */
    List<BillingSummaryResponse> getBillingsByClient(Long clientId);

    /**
     * Get billings by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of billings
     */
    List<BillingSummaryResponse> getBillingsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get unpaid billings
     * 
     * @return List of unpaid billings
     */
    List<BillingSummaryResponse> getUnpaidBillings();

    /**
     * Record a payment for a billing
     * 
     * @param id Billing ID
     * @param amount Payment amount
     * @return Updated billing
     */
    BillingResponse recordPayment(Long id, Double amount);

    /**
     * Delete a billing record
     * 
     * @param id Billing ID
     */
    void deleteBilling(Long id);

    /**
     * Check if bill number exists
     * 
     * @param billNo Bill number
     * @return true if exists, false otherwise
     */
    boolean existsByBillNo(String billNo);

    /**
     * Calculate GST for a billing amount
     * 
     * @param amount Base amount
     * @param gstRate GST rate (e.g., 18 for 18%)
     * @param isInterState true for IGST, false for CGST+SGST
     * @return Array [cgst, sgst, igst]
     */
    /**
     * Generate bill content preview for a client
     * Used to show bill preview when client is selected in the form
     * 
     * @param clientId Client ID
     * @return Generated bill preview content
     */
    String generateBillPreview(String clientId);
    Double[] calculateGst(Double amount, Double gstRate, boolean isInterState);
}

// Made with Bob
