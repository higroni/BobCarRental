package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.BillingMapper;
import com.bobcarrental.dto.request.BillingRequest;
import com.bobcarrental.dto.response.BillingResponse;
import com.bobcarrental.dto.response.BillingSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.Billing;
import com.bobcarrental.model.Client;
import com.bobcarrental.model.HeaderTemplate;
import com.bobcarrental.model.TripSheet;
import com.bobcarrental.repository.BillingRepository;
import com.bobcarrental.repository.ClientRepository;
import com.bobcarrental.repository.HeaderTemplateRepository;
import com.bobcarrental.repository.TripSheetRepository;
import com.bobcarrental.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Implementation of BillingService
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final TripSheetRepository tripSheetRepository;
    private final ClientRepository clientRepository;
    private final HeaderTemplateRepository headerTemplateRepository;
    private final BillingMapper billingMapper;

    @Override
    public BillingResponse createBilling(BillingRequest request) {
        log.info("Creating new billing: {}", request.getBillNum());

        // Validate bill number uniqueness
        if (billingRepository.findByBillNum(request.getBillNum()).isPresent()) {
            throw new ValidationException("Bill number already exists: " + request.getBillNum());
        }

        // Create billing
        Billing billing = billingMapper.toEntity(request);

        // Validate trip sheet exists (if trpNum is provided)
        if (request.getTrpNum() != null) {
            TripSheet tripSheet = tripSheetRepository.findByTrpNum(request.getTrpNum())
                    .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", request.getTrpNum()));

            // Validate trip sheet is not already billed
            if (Boolean.TRUE.equals(tripSheet.getIsBilled())) {
                throw new ValidationException("Trip sheet " + request.getTrpNum() +
                    " is already billed with bill number: " + tripSheet.getBillNum());
            }

            // Note: status field in TripSheet represents fare type (F/S/O), not trip state
            // Legacy system doesn't track trip completion status - only isBilled flag matters
            billing.setTripSheet(tripSheet);
            billing.setTrpNum(tripSheet.getTrpNum());
            
            // Copy calculated amounts from TripSheet to Billing (from BILLER.PRG lines 52-66)
            // This populates the billing with all trip sheet charges
            log.debug("TripSheet charges: hiring={}, extra={}, halt={}, minimum={}, permit={}, misc={}",
                tripSheet.getHiring(), tripSheet.getExtra(), tripSheet.getHalt(),
                tripSheet.getMinimum(), tripSheet.getPermit(), tripSheet.getMisc());
            
            BigDecimal totalAmount = tripSheet.getTotalAmount(); // Sum of all charges
            log.info("Calculated total amount from TripSheet: {}", totalAmount);
            
            billing.setTotalAmount(totalAmount);
            billing.setBillAmt(totalAmount);
            
            // If billAmount not provided in request, use calculated total
            if (request.getBillAmount() == null || request.getBillAmount().compareTo(BigDecimal.ZERO) == 0) {
                billing.setBillAmt(totalAmount);
            } else {
                // Use provided billAmount
                billing.setBillAmt(request.getBillAmount());
                billing.setTotalAmount(request.getBillAmount());
            }
            
            // Update trip sheet to mark as billed
            tripSheet.setIsBilled(true);
            tripSheet.setBillNum(request.getBillNum());
            tripSheet.setBillDate(request.getBillDate());
            tripSheetRepository.save(tripSheet);
            
            log.info("Billing created from TripSheet {}: Total Amount = {}",
                tripSheet.getTrpNum(), totalAmount);
        }

        Billing savedBilling = billingRepository.save(billing);
        log.info("Billing saved with ID: {}, now generating bill content...", savedBilling.getId());
        
        // Generate bill content automatically
        generateBillContent(savedBilling);
        log.info("Bill content generated, billImg length: {}",
                 savedBilling.getBillImg() != null ? savedBilling.getBillImg().length() : 0);
        
        savedBilling = billingRepository.save(savedBilling);
        log.info("Billing saved again after generating content");
        
        log.info("Billing created successfully with id: {}", savedBilling.getId());

        return billingMapper.toResponse(savedBilling);
    }
    
    /**
     * Generate bill content (billImg) using HeaderTemplate
     * Mimics legacy BILLER.PRG bill generation logic
     */
    private void generateBillContent(Billing billing) {
        log.info("Generating bill content for billing: {}", billing.getBillNum());
        
        // Get header template (default or first active)
        HeaderTemplate template = headerTemplateRepository.findFirstByIsDefaultTrueOrderByIdAsc()
                .or(() -> headerTemplateRepository.findFirstByActiveTrueOrderByIdAsc())
                .orElse(null);
        
        if (template == null) {
            log.warn("No header template found, bill content will be empty");
            billing.setBillImg("");
            return;
        }
        
        // Get client details for placeholder replacement
        Client client = clientRepository.findByClientId(billing.getClientId()).orElse(null);
        
        StringBuilder billContent = new StringBuilder();
        
        // Prepare variables for template rendering
        java.util.Map<String, String> variables = new java.util.HashMap<>();
        if (client != null) {
            variables.put("COMPANY_NAME", client.getClientName() != null ? client.getClientName() : "");
            variables.put("ADDRESS_LINE1", client.getAddress1() != null ? client.getAddress1() : "");
            variables.put("ADDRESS_LINE2", client.getAddress2() != null ? client.getAddress2() : "");
            variables.put("CITY", client.getCity() != null ? client.getCity() : "");
            variables.put("PIN_CODE", client.getPinCode() != null ? client.getPinCode().toString() : "");
            variables.put("PHONE", client.getPhone() != null ? client.getPhone() : "");
            variables.put("FAX", client.getFax() != null ? client.getFax() : "");
        }
        
        // Add header from template with placeholders replaced
        billContent.append(template.render(variables)).append("\n\n");
        
        // Add bill details
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        billContent.append("BILL NO: ").append(billing.getBillNum()).append("\n");
        billContent.append("DATE: ").append(billing.getBillDate().format(dateFormatter)).append("\n");
        billContent.append("_____________________________________________________________________\n\n");
        
        // Add client details (client already loaded above for template rendering)
        if (client != null) {
            billContent.append("CLIENT: ").append(client.getClientName()).append("\n");
            if (client.getAddress1() != null) {
                billContent.append("        ").append(client.getAddress1()).append("\n");
            }
            if (client.getAddress2() != null) {
                billContent.append("        ").append(client.getAddress2()).append("\n");
            }
            if (client.getPhone() != null) {
                billContent.append("PHONE:  ").append(client.getPhone()).append("\n");
            }
        } else {
            billContent.append("CLIENT: ").append(billing.getClientId()).append("\n");
        }
        
        billContent.append("_____________________________________________________________________\n\n");
        
        // Add trip sheet details if available
        TripSheet tripSheet = billing.getTripSheet();
        if (tripSheet != null) {
            billContent.append("TRIP SHEET NO: ").append(tripSheet.getTrpNum()).append("\n");
            billContent.append("TRIP DATE: ").append(tripSheet.getTrpDate().format(dateFormatter)).append("\n");
            billContent.append("VEHICLE: ").append(tripSheet.getTypeId()).append(" - ").append(tripSheet.getRegNum()).append("\n");
            billContent.append("DRIVER: ").append(tripSheet.getDriver() != null ? tripSheet.getDriver() : "N/A").append("\n\n");
            
            billContent.append("JOURNEY DETAILS:\n");
            billContent.append("  Start: ").append(tripSheet.getStartDt().format(dateFormatter))
                      .append(" ").append(tripSheet.getStartTm())
                      .append(" - ").append(tripSheet.getStartKm()).append(" km\n");
            billContent.append("  End:   ").append(tripSheet.getEndDt().format(dateFormatter))
                      .append(" ").append(tripSheet.getEndTm())
                      .append(" - ").append(tripSheet.getEndKm()).append(" km\n");
            billContent.append("  Total: ").append(tripSheet.getEndKm() - tripSheet.getStartKm()).append(" km\n\n");
            
            // Add fare breakdown
            billContent.append("FARE BREAKDOWN:\n");
            if (tripSheet.getHiring() != null && tripSheet.getHiring().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Hiring Charges:  ").append(currencyFormat.format(tripSheet.getHiring())).append("\n");
            }
            if (tripSheet.getExtra() != null && tripSheet.getExtra().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Extra Charges:   ").append(currencyFormat.format(tripSheet.getExtra())).append("\n");
            }
            if (tripSheet.getMinimum() != null && tripSheet.getMinimum().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Minimum Charges: ").append(currencyFormat.format(tripSheet.getMinimum())).append("\n");
            }
            if (tripSheet.getHalt() != null && tripSheet.getHalt().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Halt Charges:    ").append(currencyFormat.format(tripSheet.getHalt())).append("\n");
            }
            if (tripSheet.getPermit() != null && tripSheet.getPermit().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Permit Charges:  ").append(currencyFormat.format(tripSheet.getPermit())).append("\n");
            }
            if (tripSheet.getMisc() != null && tripSheet.getMisc().compareTo(BigDecimal.ZERO) > 0) {
                billContent.append("  Misc Charges:    ").append(currencyFormat.format(tripSheet.getMisc())).append("\n");
            }
            billContent.append("_____________________________________________________________________\n");
        }
        
        // Add total
        billContent.append("\nTOTAL AMOUNT: ").append(currencyFormat.format(billing.getBillAmt())).append("\n");
        
        // Add GST if applicable
        if (billing.getCgst() != null && billing.getCgst().compareTo(BigDecimal.ZERO) > 0) {
            billContent.append("CGST: ").append(currencyFormat.format(billing.getCgst())).append("\n");
        }
        if (billing.getSgst() != null && billing.getSgst().compareTo(BigDecimal.ZERO) > 0) {
            billContent.append("SGST: ").append(currencyFormat.format(billing.getSgst())).append("\n");
        }
        if (billing.getIgst() != null && billing.getIgst().compareTo(BigDecimal.ZERO) > 0) {
            billContent.append("IGST: ").append(currencyFormat.format(billing.getIgst())).append("\n");
        }
        
        billContent.append("_____________________________________________________________________\n\n");
        billContent.append("Thank you for your business!\n");
        
        billing.setBillImg(billContent.toString());
        log.info("Bill content generated successfully");
    }

    @Override
    public BillingResponse updateBilling(Long id, BillingRequest request) {
        log.info("Updating billing with id: {}", id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", id));

        // Validate bill number uniqueness (if changed)
        if (!billing.getBillNum().equals(request.getBillNum())) {
            if (billingRepository.findByBillNum(request.getBillNum()).isPresent()) {
                throw new ValidationException("Bill number already exists: " + request.getBillNum());
            }
        }

        // Validate trip sheet exists (if changed and provided)
        if (request.getTrpNum() != null) {
            Integer currentTrpNum = billing.getTripSheet() != null ? billing.getTripSheet().getTrpNum() : null;
            if (!request.getTrpNum().equals(currentTrpNum)) {
                TripSheet tripSheet = tripSheetRepository.findByTrpNum(request.getTrpNum())
                        .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", request.getTrpNum()));
                
                // Note: status field represents fare type (F/S/O), not trip state
                billing.setTripSheet(tripSheet);
            }
        } else {
            billing.setTripSheet(null);
        }

        // Update billing fields
        billingMapper.updateEntityFromRequest(request, billing);

        Billing updatedBilling = billingRepository.save(billing);
        log.info("Billing updated successfully with id: {}", updatedBilling.getId());

        return billingMapper.toResponse(updatedBilling);
    }

    @Override
    public String generateBillPreview(String clientId) {
        log.info("Generating bill preview for client: {}", clientId);
        
        // Get header template (default or first active)
        HeaderTemplate template = headerTemplateRepository.findFirstByIsDefaultTrueOrderByIdAsc()
                .or(() -> headerTemplateRepository.findFirstByActiveTrueOrderByIdAsc())
                .orElse(null);
        
        if (template == null) {
            log.warn("No header template found, returning empty preview");
            return "";
        }
        
        // Get client details for placeholder replacement
        Client client = clientRepository.findByClientId(clientId).orElse(null);
        
        if (client == null) {
            log.warn("Client not found: {}, returning template without replacements", clientId);
            return template.getTemplateContent();
        }
        
        // Prepare variables for template rendering
        java.util.Map<String, String> variables = new java.util.HashMap<>();
        variables.put("COMPANY_NAME", client.getClientName() != null ? client.getClientName() : "");
        variables.put("ADDRESS_LINE1", client.getAddress1() != null ? client.getAddress1() : "");
        variables.put("ADDRESS_LINE2", client.getAddress2() != null ? client.getAddress2() : "");
        variables.put("CITY", client.getCity() != null ? client.getCity() : "");
        variables.put("PIN_CODE", client.getPinCode() != null ? client.getPinCode().toString() : "");
        variables.put("PHONE", client.getPhone() != null ? client.getPhone() : "");
        variables.put("FAX", client.getFax() != null ? client.getFax() : "");
        
        // Render template with client data
        String preview = template.render(variables);
        
        log.info("Bill preview generated successfully for client: {}", clientId);
        return preview;
    }

    @Override
    @Transactional(readOnly = true)
    public BillingResponse getBillingById(Long id) {
        log.info("Fetching billing with id: {}", id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", id));

        return billingMapper.toResponse(billing);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingResponse getBillingByBillNo(String billNo) {
        log.info("Fetching billing with bill number: {}", billNo);

        Billing billing = billingRepository.findByBillNum(Integer.parseInt(billNo))
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "billNo", billNo));

        return billingMapper.toResponse(billing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingSummaryResponse> getAllBillings(Pageable pageable) {
        log.info("Fetching all billings with pagination");

        return billingRepository.findAll(pageable)
                .map(billingMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingSummaryResponse> getBillingsByTripSheet(Long tripSheetId) {
        log.info("Fetching billings for trip sheet id: {}", tripSheetId);

        TripSheet tripSheet = tripSheetRepository.findById(tripSheetId)
                .orElseThrow(() -> new ResourceNotFoundException("TripSheet", "id", tripSheetId));

        // Use trpNum from TripSheet to find billing
        return billingRepository.findByTrpNum(tripSheet.getTrpNum())
                .map(billing -> billingMapper.toSummaryResponse(billing))
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingSummaryResponse> getBillingsByClient(Long clientId) {
        log.info("Fetching billings for client id: {}", clientId);

        return billingRepository.findAll().stream()
                .filter(billing -> billing.getTripSheet().getBooking().getClient().getId().equals(clientId))
                .map(billingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingSummaryResponse> getBillingsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching billings between {} and {}", startDate, endDate);

        return billingRepository.findByBillDateBetween(startDate, endDate).stream()
                .map(billingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingSummaryResponse> getUnpaidBillings() {
        log.info("Fetching unpaid billings");

        return billingRepository.findAll().stream()
                .filter(billing -> {
                    double grandTotal = calculateGrandTotal(billing);
                    double paid = billing.getPaid() != null ? billing.getPaid().doubleValue() : 0.0;
                    return grandTotal - paid > 0.01; // Unpaid or partially paid
                })
                .map(billingMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BillingResponse recordPayment(Long id, Double amount) {
        log.info("Recording payment of {} for billing id: {}", amount, id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", id));

        if (amount <= 0) {
            throw new ValidationException("Payment amount must be positive");
        }

        double currentPaid = billing.getPaid() != null ? billing.getPaid().doubleValue() : 0.0;
        double grandTotal = calculateGrandTotal(billing);
        double newPaid = currentPaid + amount;

        if (newPaid > grandTotal + 0.01) { // Allow 1 paisa tolerance
            throw new ValidationException("Payment amount exceeds bill total");
        }

        billing.setPaid(BigDecimal.valueOf(newPaid));
        Billing updatedBilling = billingRepository.save(billing);

        log.info("Payment recorded successfully. New paid amount: {}", newPaid);
        return billingMapper.toResponse(updatedBilling);
    }

    @Override
    public void deleteBilling(Long id) {
        log.info("Deleting billing with id: {}", id);

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", id));

        billingRepository.delete(billing);
        log.info("Billing deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBillNo(String billNo) {
        return billingRepository.findByBillNum(Integer.parseInt(billNo)).isPresent();
    }

    @Override
    public Double[] calculateGst(Double amount, Double gstRate, boolean isInterState) {
        if (amount == null || amount <= 0 || gstRate == null || gstRate <= 0) {
            return new Double[]{0.0, 0.0, 0.0};
        }

        double gstAmount = (amount * gstRate) / 100.0;

        if (isInterState) {
            // IGST (Inter-state)
            return new Double[]{0.0, 0.0, gstAmount};
        } else {
            // CGST + SGST (Intra-state) - split equally
            double halfGst = gstAmount / 2.0;
            return new Double[]{halfGst, halfGst, 0.0};
        }
    }

    /**
     * Helper method to calculate grand total
     */
    private double calculateGrandTotal(Billing billing) {
        double total = billing.getTotalAmount() != null ? billing.getTotalAmount().doubleValue() : 0.0;
        double cgst = billing.getCgst() != null ? billing.getCgst().doubleValue() : 0.0;
        double sgst = billing.getSgst() != null ? billing.getSgst().doubleValue() : 0.0;
        double igst = billing.getIgst() != null ? billing.getIgst().doubleValue() : 0.0;
        return total + cgst + sgst + igst;
    }
}

// Made with Bob
