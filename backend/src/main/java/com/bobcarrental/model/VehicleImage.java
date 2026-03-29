package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * VehicleImage entity - NEW feature for modern application
 * Stores vehicle images with BLOB storage
 * Includes thumbnail generation for mobile optimization
 */
@Entity
@Table(name = "vehicle_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Vehicle type is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @NotBlank(message = "Image name is required")
    @Size(max = 255, message = "Image name must not exceed 255 characters")
    @Column(name = "image_name", nullable = false, length = 255)
    private String imageName;

    @NotBlank(message = "Content type is required")
    @Size(max = 50, message = "Content type must not exceed 50 characters")
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Full-size image data stored as BLOB
     * Max 5MB as per configuration
     */
    @Lob
    @Column(name = "image_data", nullable = false, columnDefinition = "BLOB")
    private byte[] imageData;

    /**
     * Thumbnail image data for mobile optimization
     * Generated automatically on upload
     * Typically 200x200 or 300x300 pixels
     */
    @Lob
    @Column(name = "thumbnail_data", columnDefinition = "BLOB")
    private byte[] thumbnailData;

    @Column(name = "thumbnail_size")
    private Long thumbnailSize;

    @Column(name = "upload_date", nullable = false)
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "thumbnail_width")
    private Integer thumbnailWidth;

    @Column(name = "thumbnail_height")
    private Integer thumbnailHeight;

    /**
     * Display order for image gallery
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * Is this the primary/featured image for the vehicle type
     */
    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "description", length = 500)
    private String description;

    // Helper methods
    public String getFileExtension() {
        if (imageName != null && imageName.contains(".")) {
            return imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    public boolean isImage() {
        String ext = getFileExtension();
        return ext.equals("jpg") || ext.equals("jpeg") || 
               ext.equals("png") || ext.equals("webp");
    }

    public String getFileSizeFormatted() {
        if (fileSize == null) return "0 B";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    public boolean hasThumbnail() {
        return thumbnailData != null && thumbnailData.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleImage)) return false;
        VehicleImage that = (VehicleImage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VehicleImage{" +
                "id=" + id +
                ", imageName='" + imageName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + getFileSizeFormatted() +
                ", hasThumbnail=" + hasThumbnail() +
                ", isPrimary=" + isPrimary +
                ", uploadDate=" + uploadDate +
                '}';
    }
}

// Made with Bob
