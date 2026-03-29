package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * VehicleType entity - migrated from VEHTYPE.DBF
 * Represents types/categories of vehicles (e.g., AMB, CON, etc.)
 */
@Entity
@Table(name = "vehicle_types",
       uniqueConstraints = @UniqueConstraint(columnNames = "type_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Type ID is required")
    @Size(max = 5, message = "Type ID must not exceed 5 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Type ID must be uppercase alphanumeric")
    @Column(name = "type_id", nullable = false, unique = true, length = 5)
    private String typeId;

    @NotBlank(message = "Type description is required")
    @Size(max = 50, message = "Type description must not exceed 50 characters")
    @Column(name = "type_desc", nullable = false, length = 50)
    private String typeDesc;

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

    /**
     * Images associated with this vehicle type
     * One vehicle type can have multiple images
     */
    @OneToMany(mappedBy = "vehicleType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleImage> images = new ArrayList<>();

    /**
     * Bookings associated with this vehicle type
     */
    @OneToMany(mappedBy = "vehicleType", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Helper methods
    public String getName() {
        return typeDesc;
    }

    public void addImage(VehicleImage image) {
        images.add(image);
        image.setVehicleType(this);
    }

    public void removeImage(VehicleImage image) {
        images.remove(image);
        image.setVehicleType(null);
    }

    public int getImageCount() {
        return images != null ? images.size() : 0;
    }

    /**
     * Alias for typeDesc field - used by services
     */
    public String getTypeName() {
        return typeDesc;
    }

    public void setTypeName(String typeName) {
        this.typeDesc = typeName;
    }

    /**
     * Check if vehicle type is active (not deleted)
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
        if (!(o instanceof VehicleType)) return false;
        VehicleType that = (VehicleType) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VehicleType{" +
                "id=" + id +
                ", typeId='" + typeId + '\'' +
                ", typeDesc='" + typeDesc + '\'' +
                ", imageCount=" + getImageCount() +
                ", deleted=" + deleted +
                '}';
    }
}

// Made with Bob
