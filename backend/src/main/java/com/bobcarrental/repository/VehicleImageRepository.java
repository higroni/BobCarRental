package com.bobcarrental.repository;

import com.bobcarrental.model.VehicleImage;
import com.bobcarrental.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository za VehicleImage entitet
 * Upravlja slikama vozila
 */
@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    
    /**
     * Pronalazi sve slike za određeni tip vozila
     * VehicleImage ima: vehicleType, imageUrl, caption, displayOrder
     */
    List<VehicleImage> findByVehicleType(VehicleType vehicleType);
    
    /**
     * Pronalazi sve slike za tip vozila po ID-u
     */
    List<VehicleImage> findByVehicleTypeId(Long vehicleTypeId);
    
    /**
     * Broji slike za određeni tip vozila
     */
    long countByVehicleType(VehicleType vehicleType);
    
    /**
     * Broji slike po tipu vozila ID-u
     */
    long countByVehicleTypeId(Long vehicleTypeId);
    
    /**
     * Pronalazi slike za tip vozila sortirane po display order-u
     */
    List<VehicleImage> findByVehicleTypeIdOrderByDisplayOrderAsc(Long vehicleTypeId);
    
    /**
     * Pronalazi slike po opisu
     */
    List<VehicleImage> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Proverava da li postoji slika sa datim display order-om za tip vozila
     */
    boolean existsByVehicleTypeIdAndDisplayOrder(Long vehicleTypeId, Integer displayOrder);
    
    /**
     * Briše sve slike za određeni tip vozila
     */
    void deleteByVehicleTypeId(Long vehicleTypeId);
}

// Made with Bob
