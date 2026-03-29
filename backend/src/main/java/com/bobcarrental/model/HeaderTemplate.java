package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * HeaderTemplate entity - migrated from HEADER.TXT
 * Represents header templates for bills/invoices
 * ADMIN ONLY access - replaces text file with database CRUD
 */
@Entity
@Table(name = "header_templates",
       indexes = {
           @Index(name = "idx_template_name", columnList = "template_name"),
           @Index(name = "idx_active", columnList = "active"),
           @Index(name = "idx_is_default", columnList = "is_default")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeaderTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Template name (e.g., "Default", "Invoice", "Confirmation Order")
     */
    @NotBlank(message = "Template name is required")
    @Size(max = 50, message = "Template name must not exceed 50 characters")
    @Column(name = "template_name", nullable = false, length = 50)
    private String templateName;

    /**
     * Template content (TEXT field)
     * Supports placeholders: {COMPANY_NAME}, {ADDRESS}, {PHONE}, etc.
     * 
     * Example from HEADER.TXT:
     *                          Bob Demo Travel    Ph:8280737
     *                        76, G.N.Chetty Road,                  8283440
     *                        T.Nagar, Madras - 17.
     *               [Govt. Authorised Tourist Car Operators]
     * _____________________________________________________________________
     */
    @NotBlank(message = "Template content is required")
    @Column(name = "template_content", nullable = false, columnDefinition = "TEXT")
    private String templateContent;

    /**
     * Description of this template
     */
    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * Is this template active?
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Is this the default template?
     * Only one template can be default at a time
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Template type
     */
    @Column(name = "template_type", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TemplateType templateType = TemplateType.INVOICE;

    /**
     * Number of lines in the template
     * Used for formatting
     */
    @Column(name = "line_count")
    private Integer lineCount;

    public enum TemplateType {
        INVOICE,              // Bill/Invoice header
        CONFIRMATION_ORDER,   // Trip sheet/Confirmation order header
        RECEIPT,             // Receipt header
        STATEMENT,           // Account statement header
        CUSTOM               // Custom template
    }

    // Helper methods
    public boolean isInvoiceTemplate() {
        return TemplateType.INVOICE.equals(templateType);
    }

    public boolean isConfirmationOrderTemplate() {
        return TemplateType.CONFIRMATION_ORDER.equals(templateType);
    }

    public boolean isReceiptTemplate() {
        return TemplateType.RECEIPT.equals(templateType);
    }

    public boolean isStatementTemplate() {
        return TemplateType.STATEMENT.equals(templateType);
    }

    /**
     * Replace placeholders in template with actual values
     */
    public String render(java.util.Map<String, String> variables) {
        String rendered = templateContent;
        
        if (variables != null) {
            for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                rendered = rendered.replace(placeholder, entry.getValue());
            }
        }
        
        return rendered;
    }

    /**
     * Get list of placeholders used in this template
     */
    public java.util.List<String> getPlaceholders() {
        java.util.List<String> placeholders = new java.util.ArrayList<>();
        String content = templateContent;
        
        int start = 0;
        while ((start = content.indexOf("{", start)) != -1) {
            int end = content.indexOf("}", start);
            if (end != -1) {
                String placeholder = content.substring(start + 1, end);
                if (!placeholders.contains(placeholder)) {
                    placeholders.add(placeholder);
                }
                start = end + 1;
            } else {
                break;
            }
        }
        
        return placeholders;
    }

    /**
     * Calculate line count from content
     */
    public void calculateLineCount() {
        if (templateContent != null) {
            this.lineCount = (int) templateContent.lines().count();
        } else {
            this.lineCount = 0;
        }
    }

    /**
     * Alias for active field - used by services
     */
    public void setIsActive(boolean isActive) {
        this.active = isActive;
    }

    public boolean getIsActive() {
        return active != null && active;
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calculateLineCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeaderTemplate)) return false;
        HeaderTemplate that = (HeaderTemplate) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "HeaderTemplate{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", templateType=" + templateType +
                ", active=" + active +
                ", isDefault=" + isDefault +
                ", lineCount=" + lineCount +
                '}';
    }
}

// Made with Bob
