package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Billing entity - migrated from BILLING.DBF
 * Represents bills/invoices generated from trip sheets
 */
@Entity
@Table(name = "billings",
       uniqueConstraints = @UniqueConstraint(columnNames = "bill_num"),
       indexes = {
           @Index(name = "idx_bill_num", columnList = "bill_num"),
           @Index(name = "idx_bill_date", columnList = "bill_date"),
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_trp_num", columnList = "trp_num"),
           @Index(name = "idx_cancelled", columnList = "cancelled")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bill number (PRIMARY KEY in legacy)
     * Auto-generated, sequential
     */
    @NotNull(message = "Bill number is required")
    @Column(name = "bill_num", nullable = false, unique = true)
    private Integer billNum;

    /**
     * Bill date
     */
    @NotNull(message = "Bill date is required")
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    /**
     * Client ID (Foreign Key -> CLIENT)
     * Can be "MISC" for one-time clients
     */
    @NotNull(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Column(name = "client_id", nullable = false, length = 10)
    private String clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_fk", referencedColumnName = "id")
    private Client client;

    /**
     * Bill image/content (TEXT field)
     * Generated bill in text format (migrated from MEMO)
     * Generated automatically on first view
     */
    @Column(name = "bill_img", columnDefinition = "TEXT")
    private String billImg;

    /**
     * Trip sheet number (Foreign Key -> TRPSHEET)
     * 1:1 relationship
     * Optional - billing can exist without trip sheet in legacy system
     */
    @Column(name = "trp_num")
    private Integer trpNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_sheet_fk", referencedColumnName = "id")
    private TripSheet tripSheet;

    /**
     * Has this bill been printed?
     * First print = ORIGINAL, subsequent = COPY
     */
    @Column(name = "printed", nullable = false)
    @Builder.Default
    private Boolean printed = false;

    /**
     * Is this bill cancelled?
     * Cancelled bills create ACCOUNTS entry with Recd=BillAmt
     * Bills are NEVER physically deleted
     */
    @Column(name = "cancelled", nullable = false)
    @Builder.Default
    private Boolean cancelled = false;

    /**
     * Bill amount (total)
     * Optional in entity but required in BillingRequest DTO
     */
    @Column(name = "bill_amt", precision = 10, scale = 2)
    private BigDecimal billAmt;

    /**
     * Bill number as string (alternative field name)
     */
    @Column(name = "bill_no", length = 20)
    private String billNo;

    /**
     * Total amount (alternative field name)
     */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * CGST amount
     */
    @Column(name = "cgst", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cgst = BigDecimal.ZERO;

    /**
     * SGST amount
     */
    @Column(name = "sgst", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal sgst = BigDecimal.ZERO;

    /**
     * IGST amount
     */
    @Column(name = "igst", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal igst = BigDecimal.ZERO;

    /**
     * Paid amount
     */
    @Column(name = "paid", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paid = BigDecimal.ZERO;

    /**
     * Tagged for filtering/reporting
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    // Helper methods
    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public String getBillNumber() {
        if (billDate != null && billNum != null) {
            String month = billDate.getMonth().toString().substring(0, 3);
            String year = String.valueOf(billDate.getYear()).substring(2);
            return month + "/" + billNum + "/" + year;
        }
        return String.valueOf(billNum);
    }

    public String getPrintStatus() {
        if (cancelled) {
            return "CANCELLED";
        } else if (printed) {
            return "COPY";
        } else {
            return "ORIGINAL";
        }
    }

    public boolean needsBillGeneration() {
        return billImg == null || billImg.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Billing)) return false;
        Billing billing = (Billing) o;
        return id != null && id.equals(billing.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Billing{" +
                "id=" + id +
                ", billNum=" + billNum +
                ", billDate=" + billDate +
                ", clientId='" + clientId + '\'' +
                ", trpNum=" + trpNum +
                ", billAmt=" + billAmt +
                ", printed=" + printed +
                ", cancelled=" + cancelled +
                '}';
    }
}

// Made with Bob
