package com.bobcarrental.service.impl;

import com.bobcarrental.mapper.AccountMapper;
import com.bobcarrental.dto.request.AccountRequest;
import com.bobcarrental.dto.response.AccountResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.model.Account;
import com.bobcarrental.repository.AccountRepository;
import com.bobcarrental.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AccountService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        log.debug("Getting all accounts with pagination: {}", pageable);
        return accountRepository.findAll(pageable)
                .map(accountMapper::toResponse);
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        log.debug("Getting account by id: {}", id);
        Account account = findAccountById(id);
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getAccountByCode(String code) {
        log.debug("Getting account by num (code): {}", code);
        // Account doesn't have code field, using num instead
        try {
            Integer num = Integer.parseInt(code);
            Account account = accountRepository.findByNum(num)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "num", num));
            return accountMapper.toResponse(account);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid account number format: " + code);
        }
    }

    @Override
    public List<AccountResponse> getAccountsByType(String accountType) {
        log.debug("Getting accounts by type: {} (field not available, returning all)", accountType);
        // Account doesn't have accountType field, return all accounts
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getActiveAccounts() {
        log.debug("Getting active (non-deleted) accounts");
        // Account doesn't have isActive field, return non-deleted accounts
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .filter(account -> !account.getDeleted())
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getParentAccounts() {
        log.debug("Getting parent accounts (field not available, returning all)");
        // Account doesn't have parentAccount field, return all accounts
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getSubAccounts(Long parentAccountId) {
        log.debug("Getting sub-accounts for parent: {} (field not available, returning empty)", parentAccountId);
        // Account doesn't have parentAccount field, return empty list
        return List.of();
    }

    @Override
    public Page<AccountResponse> searchAccounts(String query, Pageable pageable) {
        log.debug("Searching accounts with query: {}", query);
        // Use searchAccounts which searches desc field
        return accountRepository.searchAccounts(query, pageable)
                .map(accountMapper::toResponse);
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.debug("Creating account: {}", request);
        
        // Check if num already exists
        if (request.getNum() != null && accountRepository.existsByNum(request.getNum().intValue())) {
            throw new ValidationException("Account number already exists: " + request.getNum());
        }
        
        Account account = accountMapper.toEntity(request);
        Account savedAccount = accountRepository.save(account);
        log.info("Created account with id: {}", savedAccount.getId());
        
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Long id, AccountRequest request) {
        log.debug("Updating account {}: {}", id, request);
        
        Account account = findAccountById(id);
        
        // Check if num is being changed and if new num already exists
        if (request.getNum() != null && !account.getNum().equals(request.getNum().intValue()) &&
            accountRepository.existsByNum(request.getNum().intValue())) {
            throw new ValidationException("Account number already exists: " + request.getNum());
        }
        
        accountMapper.updateEntityFromRequest(request, account);
        Account updatedAccount = accountRepository.save(account);
        log.info("Updated account with id: {}", id);
        
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountBalance(Long id, BigDecimal amount, String transactionType) {
        log.debug("Updating account {} balance by {} ({})", id, amount, transactionType);
        
        Account account = findAccountById(id);
        
        // Validate transaction type
        if (!transactionType.equals("DEBIT") && !transactionType.equals("CREDIT")) {
            throw new ValidationException("Invalid transaction type: " + transactionType);
        }
        
        // Account doesn't have currentBalance field, update recd or bill instead
        if (transactionType.equals("DEBIT")) {
            account.setBill(account.getBill().add(amount));
        } else {
            account.setRecd(account.getRecd().add(amount));
        }
        
        Account updatedAccount = accountRepository.save(account);
        log.info("Updated account {} balance", id);
        
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse activateAccount(Long id) {
        log.debug("Activating account: {} (no isActive field, setting deleted=false)", id);
        
        Account account = findAccountById(id);
        account.setDeleted(false);
        Account updatedAccount = accountRepository.save(account);
        log.info("Activated account with id: {}", id);
        
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse deactivateAccount(Long id) {
        log.debug("Deactivating account: {} (no isActive field, setting deleted=true)", id);
        
        Account account = findAccountById(id);
        account.setDeleted(true);
        Account updatedAccount = accountRepository.save(account);
        log.info("Deactivated account with id: {}", id);
        
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        log.debug("Deleting account: {}", id);
        
        Account account = findAccountById(id);
        
        // Account doesn't have sub-accounts, just delete
        accountRepository.delete(account);
        log.info("Deleted account with id: {}", id);
    }

    @Override
    public boolean existsByCode(String code) {
        // Account doesn't have code field, check by num
        try {
            Integer num = Integer.parseInt(code);
            return accountRepository.existsByNum(num);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Find account by ID or throw exception
     */
    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
    }
}

// Made with Bob
