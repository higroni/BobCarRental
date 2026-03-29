package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Address entity - migrated from ADDRESS.DBF
 * Represents address book entries (contacts for clients)
 * Multiple contacts per client
 */
@Entity
@Table(name = "addresses",
       indexes = {
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_client_name", columnList = "client_id, name"),
           @Index(name = "idx_dept", columnList = "dept")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client ID (Foreign Key -> CLIENT)
     * Can be "MISC" for one-time clients
     * Part of composite key in legacy (ClientId + Name)
     */
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Column(name = "client_id", nullable = false, length = 10)
    private String clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_fk", referencedColumnName = "id")
    private Client client;

    /**
     * Department/division
     */
    @Size(max = 15, message = "Department must not exceed 15 characters")
    @Column(name = "dept", length = 15)
    private String dept;

    /**
     * Description of this address entry
     */
    @Size(max = 10, message = "Description must not exceed 10 characters")
    @Column(name = "desc", length = 10)
    private String desc;

    /**
     * Contact person name
     * Part of composite key in legacy (ClientId + Name)
     */
    @NotBlank(message = "Name is required")
    @Size(max = 40, message = "Name must not exceed 40 characters")
    @Column(name = "name", nullable = false, length = 40)
    private String name;

    /**
     * Address lines
     */
    @Size(max = 35, message = "Address1 must not exceed 35 characters")
    @Column(name = "address1", length = 35)
    private String address1;

    @Size(max = 30, message = "Address2 must not exceed 30 characters")
    @Column(name = "address2", length = 30)
    private String address2;

    @Size(max = 25, message = "Address3 must not exceed 25 characters")
    @Column(name = "address3", length = 25)
    private String address3;

    @Size(max = 20, message = "Place must not exceed 20 characters")
    @Column(name = "place", length = 20)
    private String place;

    @Size(max = 15, message = "City must not exceed 15 characters")
    @Column(name = "city", length = 15)
    private String city;

    @Size(max = 10, message = "Pin code must not exceed 10 characters")
    @Column(name = "pin_code", length = 10)
    private String pinCode;

    /**
     * Contact information
     */
    @Size(max = 25, message = "Phone must not exceed 25 characters")
    @Column(name = "phone", length = 25)
    private String phone;

    @Size(max = 25, message = "Fax must not exceed 25 characters")
    @Column(name = "fax", length = 25)
    private String fax;

    /**
     * Tagged for filtering/reporting
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Soft delete flag
     * Legacy system didn't allow deletion, only modification
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    /**
     * Category field for filtering
     */
    @Size(max = 20, message = "Category must not exceed 20 characters")
    @Column(name = "category", length = 20)
    private String category;

    /**
     * Company name field
     */
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    @Column(name = "company_name", length = 100)
    private String companyName;

    /**
     * Active status flag
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Helper methods
    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address1 != null && !address1.trim().isEmpty()) {
            sb.append(address1);
        }
        if (address2 != null && !address2.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(address2);
        }
        if (address3 != null && !address3.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(address3);
        }
        if (place != null && !place.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(place);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (pinCode != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(pinCode);
        }
        return sb.toString();
    }

    public String getContactInfo() {
        StringBuilder sb = new StringBuilder();
        if (phone != null && !phone.trim().isEmpty()) {
            sb.append("Phone: ").append(phone);
        }
        if (fax != null && !fax.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Fax: ").append(fax);
        }
        return sb.toString();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            sb.append(name);
        }
        if (dept != null && !dept.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" (");
            sb.append(dept);
            if (sb.length() > 0) sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return id != null && id.equals(address.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", city='" + city + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}

// Made with Bob
