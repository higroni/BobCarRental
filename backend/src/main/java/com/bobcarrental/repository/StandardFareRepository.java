package com.bobcarrental.repository;

import com.bobcarrental.model.StandardFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository za StandardFare entitet
 * Upravlja standardnim cenovnicima (FARES.TXT iz legacy sistema)
 * ADMIN ONLY pristup
 */
@Repository
public interface StandardFareRepository extends JpaRepository<StandardFare, Long> {
    
    /**
     * Pronalazi cenovnike po kodu vozila
     */
    List<StandardFare> findByVehicleCode(String vehicleCode);
    
    /**
     * Pronalazi cenovnike po tipu tarife
     */
    List<StandardFare> findByFareType(StandardFare.FareType fareType);
    
    /**
     * Pronalazi cenovnike po kodu vozila i tipu tarife
     */
    List<StandardFare> findByVehicleCodeAndFareType(String vehicleCode, StandardFare.FareType fareType);
    
    /**
     * Pronalazi aktivne cenovnike (trenutno važeće)
     */
    @Query("SELECT f FROM StandardFare f WHERE " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= CURRENT_DATE) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= CURRENT_DATE)")
    List<StandardFare> findActiveFares();
    
    /**
     * Pronalazi aktivne cenovnike za određeni kod vozila
     */
    @Query("SELECT f FROM StandardFare f WHERE f.vehicleCode = :vehicleCode AND " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= CURRENT_DATE) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= CURRENT_DATE)")
    List<StandardFare> findActiveFaresByVehicleCode(@Param("vehicleCode") String vehicleCode);
    
    /**
     * Pronalazi aktivni cenovnik za vozilo i tip tarife
     */
    @Query("SELECT f FROM StandardFare f WHERE f.vehicleCode = :vehicleCode AND f.fareType = :fareType AND " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= CURRENT_DATE) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= CURRENT_DATE)")
    Optional<StandardFare> findActiveFareByVehicleCodeAndType(@Param("vehicleCode") String vehicleCode,
                                                                @Param("fareType") StandardFare.FareType fareType);
    
    /**
     * Pronalazi cenovnike važeće na određeni datum
     */
    @Query("SELECT f FROM StandardFare f WHERE " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= :date) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= :date)")
    List<StandardFare> findFaresEffectiveOnDate(@Param("date") LocalDate date);
    
    /**
     * Pronalazi sve kodove vozila (distinct)
     */
    @Query("SELECT DISTINCT f.vehicleCode FROM StandardFare f ORDER BY f.vehicleCode")
    List<String> findAllVehicleCodes();
    
    /**
     * Broji aktivne cenovnike
     */
    @Query("SELECT COUNT(f) FROM StandardFare f WHERE " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= CURRENT_DATE) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= CURRENT_DATE)")
    long countActiveFares();
    
    /**
     * Broji cenovnike po kodu vozila
     */
    long countByVehicleCode(String vehicleCode);
    
    /**
     * Proverava da li postoji aktivni cenovnik za vozilo (sa datumima)
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StandardFare f WHERE " +
           "f.vehicleCode = :vehicleCode AND " +
           "(f.effectiveFrom IS NULL OR f.effectiveFrom <= CURRENT_DATE) AND " +
           "(f.effectiveTo IS NULL OR f.effectiveTo >= CURRENT_DATE)")
    boolean hasActiveEffectiveFareForVehicle(@Param("vehicleCode") String vehicleCode);
    
    /**
     * Method required by FareCalculationServiceImpl
     * Finds fare by VehicleType entity and fare type string
     */
    @Query("SELECT f FROM StandardFare f WHERE f.vehicleCode = :#{#vehicleType.typeId} AND f.fareType = :fareType")
    Optional<StandardFare> findByVehicleTypeAndFareType(@Param("vehicleType") com.bobcarrental.model.VehicleType vehicleType,
                                                          @Param("fareType") String fareType);
    
    /**
     * Proverava da li postoji aktivni cenovnik za vozilo (samo active flag)
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StandardFare f WHERE f.vehicleCode = :vehicleCode AND f.active = true")
    boolean hasActiveFareForVehicle(@Param("vehicleCode") String vehicleCode);

    /**
     * Find fares by vehicle type ID
     */
    @Query("SELECT f FROM StandardFare f WHERE f.vehicleCode = (SELECT v.typeId FROM VehicleType v WHERE v.id = :vehicleTypeId)")
    List<StandardFare> findByVehicleTypeId(@Param("vehicleTypeId") Long vehicleTypeId);

    /**
     * Find fare by vehicle type ID and fare type string
     */
    @Query("SELECT f FROM StandardFare f WHERE f.vehicleCode = (SELECT v.typeId FROM VehicleType v WHERE v.id = :vehicleTypeId) AND str(f.fareType) = :fareType")
    Optional<StandardFare> findByVehicleTypeIdAndFareType(@Param("vehicleTypeId") Long vehicleTypeId, @Param("fareType") String fareType);

    /**
     * Find active fares (using active flag)
     */
    List<StandardFare> findByIsActiveTrue();

    /**
     * Check if fare exists by vehicle type ID and fare type
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StandardFare f WHERE f.vehicleCode = (SELECT v.typeId FROM VehicleType v WHERE v.id = :vehicleTypeId) AND str(f.fareType) = :fareType")
    boolean existsByVehicleTypeIdAndFareType(@Param("vehicleTypeId") Long vehicleTypeId, @Param("fareType") String fareType);

    /**
     * Check if fare exists by vehicle type typeId (String) and fare type
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StandardFare f WHERE f.vehicleCode = :typeId AND str(f.fareType) = :fareType")
    boolean existsByVehicleTypeTypeIdAndFareType(@Param("typeId") String typeId, @Param("fareType") String fareType);
}

// Made with Bob
