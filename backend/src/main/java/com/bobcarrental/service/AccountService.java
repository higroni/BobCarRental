package com.bobcarrental.service;

import com.bobcarrental.dto.request.AccountRequest;
import com.bobcarrental.dto.response.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for account operations
 */
public interface AccountService {

    /**
     * Get all accounts with pagination
     */
    Page<AccountResponse> getAllAccounts(Pageable pageable);

    /**
     * Get account by ID
     */
    AccountResponse getAccountById(Long id);

    /**
     * Get account by code
     */
    AccountResponse getAccountByCode(String code);

    /**
     * Get accounts by type
     */
    List<AccountResponse> getAccountsByType(String accountType);

    /**
     * Get active accounts
     */
    List<AccountResponse> getActiveAccounts();

    /**
     * Get parent accounts (accounts without parent)
     */
    List<AccountResponse> getParentAccounts();

    /**
     * Get sub-accounts for a parent account
     */
    List<AccountResponse> getSubAccounts(Long parentAccountId);

    /**
     * Search accounts by name or code
     */
    Page<AccountResponse> searchAccounts(String query, Pageable pageable);

    /**
     * Create new account
     */
    AccountResponse createAccount(AccountRequest request);

    /**
     * Update existing account
     */
    AccountResponse updateAccount(Long id, AccountRequest request);

    /**
     * Update account balance
     */
    AccountResponse updateAccountBalance(Long id, BigDecimal amount, String transactionType);

    /**
     * Activate account
     */
    AccountResponse activateAccount(Long id);

    /**
     * Deactivate account
     */
    AccountResponse deactivateAccount(Long id);

    /**
     * Delete account
     */
    void deleteAccount(Long id);

    /**
     * Check if account code exists
     */
    boolean existsByCode(String code);
}

// Made with Bob
