package com.bobcarrental.repository;

import com.bobcarrental.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository za Address entitet
 * Upravlja adresarom (ADDRESS.DBF iz legacy sistema)
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Pronalazi adrese po klijentu
     */
    List<Address> findByClientId(String clientId);
    
    /**
     * Pronalazi adrese po klijentu sa paginacijom
     */
    Page<Address> findByClientId(String clientId, Pageable pageable);
    
    /**
     * Pronalazi adrese po odeljenju/imenu (dept field)
     */
    List<Address> findByDeptContainingIgnoreCase(String dept);
    
    /**
     * Pronalazi adrese po odeljenju (exact match)
     */
    List<Address> findByDept(String dept);
    
    /**
     * Pronalazi adrese po gradu
     */
    List<Address> findByCity(String city);
    
    /**
     * Pronalazi adrese po gradu (case-insensitive)
     */
    List<Address> findByCityIgnoreCase(String city);
    
    /**
     * Pronalazi adrese po PIN kodu (String type)
     */
    List<Address> findByPinCode(String pinCode);
    
    /**
     * Pronalazi označene adrese
     */
    List<Address> findByTaggedTrue();
    
    /**
     * Pronalazi adrese po klijentu i odeljenju
     */
    List<Address> findByClientIdAndDept(String clientId, String dept);
    
    /**
     * Napredna pretraga adresa
     */
    @Query("SELECT a FROM Address a WHERE " +
           "LOWER(a.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.dept) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.desc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Address> searchAddresses(@Param("search") String search, Pageable pageable);
    
    /**
     * Pronalazi adrese po klijentu sa pretragom
     */
    @Query("SELECT a FROM Address a WHERE a.clientId = :clientId AND " +
           "(LOWER(a.dept) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.desc) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Address> searchByClient(@Param("clientId") String clientId,
                                  @Param("search") String search);
    
    /**
     * Broji adrese za klijenta
     */
    long countByClientId(String clientId);
    
    /**
     * Broji adrese po gradu
     */
    long countByCity(String city);
    
    /**
     * Pronalazi sve gradove (distinct)
     */
    @Query("SELECT DISTINCT a.city FROM Address a WHERE a.city IS NOT NULL ORDER BY a.city")
    List<String> findAllCities();
    
    /**
     * Pronalazi sva odeljenja (distinct)
     */
    @Query("SELECT DISTINCT a.dept FROM Address a WHERE a.dept IS NOT NULL ORDER BY a.dept")
    List<String> findAllDepartments();
}

// Made with Bob
