package com.bobcarrental.repository;

import com.bobcarrental.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository za VehicleType entitet
 * Upravlja tipovima vozila (VEHTYPE.DBF iz legacy sistema)
 */
@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    
    /**
     * Pronalazi tip vozila po jedinstvenom typeId (legacy ključ)
     * Koristi se za SuperCheckIt validaciju
     */
    Optional<VehicleType> findByTypeId(String typeId);
    
    /**
     * Proverava da li postoji tip vozila sa datim typeId
     * Implementacija PresenceChk validacije
     */
    boolean existsByTypeId(String typeId);
    
    /**
     * Pretraga tipova vozila po opisu
     */
    List<VehicleType> findByTypeDescContainingIgnoreCase(String typeDesc);
    
    /**
     * Pronalazi označene tipove vozila
     */
    List<VehicleType> findByTaggedTrue();
    
    /**
     * Napredna pretraga tipova vozila
     */
    @Query("SELECT v FROM VehicleType v WHERE " +
           "LOWER(v.typeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.typeDesc) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<VehicleType> searchVehicleTypes(@Param("search") String search);
    
    /**
     * Broji aktivne tipove vozila
     */
    @Query("SELECT COUNT(v) FROM VehicleType v WHERE v.tagged = false")
    long countActiveVehicleTypes();
    
    /**
     * Pronalazi sve tipove vozila sa slikama
     */
    @Query("SELECT DISTINCT v FROM VehicleType v LEFT JOIN FETCH v.images WHERE SIZE(v.images) > 0")
    List<VehicleType> findAllWithImages();
    
    /**
     * Methods required by VehicleTypeServiceImpl
     */
    @Query("SELECT v FROM VehicleType v WHERE v.deleted = false")
    List<VehicleType> findByActiveTrue();
    
    /**
     * Check if vehicle type exists by typeDesc
     */
    boolean existsByTypeDesc(String typeDesc);
    
    /**
     * Check if vehicle type exists by typeDesc excluding specific ID
     */
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VehicleType v WHERE v.typeDesc = :typeDesc AND v.id <> :id")
    boolean existsByTypeDescAndIdNot(@Param("typeDesc") String typeDesc, @Param("id") Long id);
}

// Made with Bob
