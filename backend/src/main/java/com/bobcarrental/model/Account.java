package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Account entity - migrated from ACCOUNTS.DBF
 * Represents accounting entries (receipts and bills)
 * Used for tracking payments and outstanding balances
 */
@Entity
@Table(name = "accounts",
       indexes = {
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_num", columnList = "num"),
           @Index(name = "idx_date", columnList = "date"),
           @Index(name = "idx_desc", columnList = "desc")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Description of the entry
     * Common values: "BILL", "CANCELLED BILL", "PAYMENT", "RECEIPT"
     */
    @NotBlank(message = "Description is required")
    @Size(max = 15, message = "Description must not exceed 15 characters")
    @Column(name = "desc", nullable = false, length = 15)
    private String desc;

    /**
     * Document number (bill number, receipt number, etc.)
     * Can be numeric or alphanumeric
     */
    @Column(name = "num")
    private Long num;

    /**
     * Entry date
     */
    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Client ID (Foreign Key -> CLIENT)
     * Can be "MISC" for one-time clients
     */
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Column(name = "client_id", nullable = false, length = 10)
    private String clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_fk", referencedColumnName = "id")
    private Client client;

    /**
     * Amount received (payments, cancelled bills)
     * Credit side
     */
    @Column(name = "recd", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal recd = BigDecimal.ZERO;

    /**
     * Amount billed (invoices)
     * Debit side
     */
    @Column(name = "bill", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal bill = BigDecimal.ZERO;

    /**
     * Tagged for filtering/reporting
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Soft delete flag
     * Legacy system didn't allow deletion
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    /**
     * Account code - unique identifier for chart of accounts
     */
    @Column(name = "code", unique = true, length = 20)
    private String code;

    /**
     * Account type - ASSET, LIABILITY, INCOME, EXPENSE, EQUITY
     */
    @Column(name = "account_type", length = 20)
    private String accountType;

    /**
     * Account name/description
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * Parent account for hierarchical chart of accounts
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;

    /**
     * Current balance
     */
    @Column(name = "current_balance", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;

    /**
     * Is account active
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Helper methods
    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public boolean isPayment() {
        return recd != null && recd.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isBill() {
        return bill != null && bill.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCancelledBill() {
        return "CANCELLED BILL".equalsIgnoreCase(desc);
    }

    /**
     * Get the balance impact of this entry
     * Positive = increases balance (bill)
     * Negative = decreases balance (payment)
     */
    public BigDecimal getBalanceImpact() {
        BigDecimal impact = BigDecimal.ZERO;
        if (bill != null) {
            impact = impact.add(bill);
        }
        if (recd != null) {
            impact = impact.subtract(recd);
        }
        return impact;
    }

    public String getEntryType() {
        if (isCancelledBill()) {
            return "Cancelled Bill";
        } else if (isPayment() && !isBill()) {
            return "Payment";
        } else if (isBill() && !isPayment()) {
            return "Bill";
        } else {
            return desc;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                ", num=" + num +
                ", date=" + date +
                ", clientId='" + clientId + '\'' +
                ", recd=" + recd +
                ", bill=" + bill +
                ", balanceImpact=" + getBalanceImpact() +
                '}';
    }
}

// Made with Bob
