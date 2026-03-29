package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Client entity - migrated from CLIENT.DBF
 * Represents customers/clients of the car rental business
 */
@Entity
@Table(name = "clients",
       uniqueConstraints = @UniqueConstraint(columnNames = "client_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Client ID must be uppercase alphanumeric")
    @Column(name = "client_id", nullable = false, unique = true, length = 10)
    private String clientId;

    @NotBlank(message = "Client name is required")
    @Size(max = 40, message = "Client name must not exceed 40 characters")
    @Column(name = "client_name", nullable = false, length = 40)
    private String clientName;

    @Size(max = 35)
    @Column(name = "address1", length = 35)
    private String address1;

    @Size(max = 30)
    @Column(name = "address2", length = 30)
    private String address2;

    @Size(max = 25)
    @Column(name = "address3", length = 25)
    private String address3;

    @Size(max = 20)
    @Column(name = "place", length = 20)
    private String place;

    @Size(max = 15)
    @Column(name = "city", length = 15)
    private String city;

    @Column(name = "pin_code")
    private Integer pinCode;

    @Size(max = 25)
    @Column(name = "phone", length = 25)
    private String phone;

    @Size(max = 25)
    @Column(name = "fax", length = 25)
    private String fax;

    /**
     * Custom fare/price list for this client (TEXT field)
     * Migrated from MEMO field in legacy system
     * If null or empty, uses standard fares
     */
    @Column(name = "fare", columnDefinition = "TEXT")
    private String fare;

    /**
     * Allow split rate for this client (Y/N)
     * If true, client can use split rate pricing
     */
    @Column(name = "is_split", length = 1)
    @Builder.Default
    private Boolean isSplit = false;

    /**
     * Tagged for filtering/reporting
     * Legacy feature from DBF system
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Soft delete flag
     * Legacy system didn't allow deletion, we implement soft delete
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    // Helper methods
    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public boolean hasCustomFare() {
        return fare != null && !fare.trim().isEmpty();
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

    /**
     * Alias for clientName field - used by services
     */
    public String getName() {
        return clientName;
    }

    public void setName(String name) {
        this.clientName = name;
    }

    /**
     * Check if client is active (not deleted)
     */
    public Boolean getActive() {
        return !deleted;
    }

    public void setActive(Boolean active) {
        this.deleted = !active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return id != null && id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", city='" + city + '\'' +
                ", isSplit=" + isSplit +
                ", deleted=" + deleted +
                '}';
    }
}

// Made with Bob
