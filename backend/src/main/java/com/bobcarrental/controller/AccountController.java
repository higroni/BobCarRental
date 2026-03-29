package com.bobcarrental.controller;

import com.bobcarrental.dto.request.AccountRequest;
import com.bobcarrental.dto.response.AccountResponse;
import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.service.AccountService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for account operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Get all accounts with pagination
     * GET /api/v1/accounts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> getAllAccounts(
            @PageableDefault(size = 20, sort = "code", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET /api/v1/accounts - Getting all accounts");
        Page<AccountResponse> accountPage = accountService.getAllAccounts(pageable);
        PageResponse<AccountResponse> pageResponse = PageResponse.of(accountPage);
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", pageResponse));
    }

    /**
     * Get account by ID
     * GET /api/v1/accounts/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id) {
        log.info("GET /api/v1/accounts/{} - Getting account by id", id);
        AccountResponse account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
    }

    /**
     * Get account by code
     * GET /api/v1/accounts/code/{code}
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> getAccountByCode(@PathVariable String code) {
        log.info("GET /api/v1/accounts/code/{} - Getting account by code", code);
        AccountResponse account = accountService.getAccountByCode(code);
        return ResponseEntity.ok(account);
    }

    /**
     * Get accounts by type
     * GET /api/v1/accounts/type/{accountType}
     */
    @GetMapping("/type/{accountType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAccountsByType(@PathVariable String accountType) {
        log.info("GET /api/v1/accounts/type/{} - Getting accounts by type", accountType);
        List<AccountResponse> accounts = accountService.getAccountsByType(accountType);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get active accounts
     * GET /api/v1/accounts/active
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        log.info("GET /api/v1/accounts/active - Getting active accounts");
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get parent accounts
     * GET /api/v1/accounts/parents
     */
    @GetMapping("/parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getParentAccounts() {
        log.info("GET /api/v1/accounts/parents - Getting parent accounts");
        List<AccountResponse> accounts = accountService.getParentAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get sub-accounts for a parent account
     * GET /api/v1/accounts/{parentId}/subaccounts
     */
    @GetMapping("/{parentId}/subaccounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getSubAccounts(@PathVariable Long parentId) {
        log.info("GET /api/v1/accounts/{}/subaccounts - Getting sub-accounts", parentId);
        List<AccountResponse> accounts = accountService.getSubAccounts(parentId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Search accounts by name or code
     * GET /api/v1/accounts/search?q={query}
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> searchAccounts(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "code") Pageable pageable) {
        log.info("GET /api/v1/accounts/search?q={} - Searching accounts", q);
        Page<AccountResponse> accountPage = accountService.searchAccounts(q, pageable);
        PageResponse<AccountResponse> pageResponse = PageResponse.of(accountPage);
        return ResponseEntity.ok(ApiResponse.success("Accounts found", pageResponse));
    }

    /**
     * Create new account
     * POST /api/v1/accounts
     * Requires ADMIN role
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountRequest request) {
        log.info("POST /api/v1/accounts - Creating account");
        AccountResponse createdAccount = accountService.createAccount(request);
        
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account created successfully")
                .data(createdAccount)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing account
     * PUT /api/v1/accounts/{id}
     * Requires ADMIN role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request) {
        log.info("PUT /api/v1/accounts/{} - Updating account", id);
        AccountResponse updatedAccount = accountService.updateAccount(id, request);
        
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account updated successfully")
                .data(updatedAccount)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update account balance
     * PATCH /api/v1/accounts/{id}/balance
     * Requires ADMIN role
     */
    @PatchMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> balanceUpdate) {
        log.info("PATCH /api/v1/accounts/{}/balance - Updating account balance", id);
        
        BigDecimal amount = new BigDecimal(balanceUpdate.get("amount").toString());
        String transactionType = balanceUpdate.get("transactionType").toString();
        
        AccountResponse updatedAccount = accountService.updateAccountBalance(id, amount, transactionType);
        
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account balance updated successfully")
                .data(updatedAccount)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Activate account
     * PATCH /api/v1/accounts/{id}/activate
     * Requires ADMIN role
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> activateAccount(@PathVariable Long id) {
        log.info("PATCH /api/v1/accounts/{}/activate - Activating account", id);
        AccountResponse updatedAccount = accountService.activateAccount(id);
        
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account activated successfully")
                .data(updatedAccount)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate account
     * PATCH /api/v1/accounts/{id}/deactivate
     * Requires ADMIN role
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> deactivateAccount(@PathVariable Long id) {
        log.info("PATCH /api/v1/accounts/{}/deactivate - Deactivating account", id);
        AccountResponse updatedAccount = accountService.deactivateAccount(id);
        
        ApiResponse<AccountResponse> response = ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account deactivated successfully")
                .data(updatedAccount)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete account
     * DELETE /api/v1/accounts/{id}
     * Requires ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        log.info("DELETE /api/v1/accounts/{} - Deleting account", id);
        accountService.deleteAccount(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Account deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if account code exists
     * GET /api/v1/accounts/exists/{code}
     */
    @GetMapping("/exists/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkAccountCodeExists(@PathVariable String code) {
        log.info("GET /api/v1/accounts/exists/{} - Checking if account code exists", code);
        boolean exists = accountService.existsByCode(code);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}

// Made with Bob
