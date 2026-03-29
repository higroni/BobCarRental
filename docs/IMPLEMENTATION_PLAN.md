# BobCarRental - Detailed Implementation Plan

## 📋 Document Overview

This document provides a step-by-step implementation guide for continuing the BobCarRental migration project. It includes detailed architecture, code examples, and implementation order.

**Created**: 2026-03-26  
**Status**: Active Development Guide  
**Target Completion**: 55-70 days

---

## 🎯 Current Project State

### ✅ Completed Components

#### 1. Project Structure
```
bobcarrental/
├── backend/
│   ├── pom.xml                    ✅ Complete with all dependencies
│   └── src/main/
│       ├── java/com/bobcarrental/
│       │   ├── BobCarRentalApplication.java  ✅ Main class
│       │   └── model/             ✅ All 14 entities complete
│       └── resources/
│           └── application.properties  ✅ Full configuration
├── frontend/                      ❌ Empty (to be created)
├── migration/                     ❌ Empty (to be created)
└── docs/                          ✅ Documentation exists
```

#### 2. Entity Models (14 entities)
- ✅ [`BaseEntity`](../backend/src/main/java/com/bobcarrental/model/BaseEntity.java) - Base class with audit fields
- ✅ [`User`](../backend/src/main/java/com/bobcarrental/model/User.java) - Authentication entity
- ✅ [`Role`](../backend/src/main/java/com/bobcarrental/model/Role.java) - RBAC roles
- ✅ [`Client`](../backend/src/main/java/com/bobcarrental/model/Client.java) - Customer management
- ✅ [`VehicleType`](../backend/src/main/java/com/bobcarrental/model/VehicleType.java) - Fleet types
- ✅ [`VehicleImage`](../backend/src/main/java/com/bobcarrental/model/VehicleImage.java) - Vehicle photos
- ✅ [`Booking`](../backend/src/main/java/com/bobcarrental/model/Booking.java) - Reservations
- ✅ [`TripSheet`](../backend/src/main/java/com/bobcarrental/model/TripSheet.java) - Trip confirmations
- ✅ [`Billing`](../backend/src/main/java/com/bobcarrental/model/Billing.java) - Invoices
- ✅ [`Account`](../backend/src/main/java/com/bobcarrental/model/Account.java) - Financial records
- ✅ [`Address`](../backend/src/main/java/com/bobcarrental/model/Address.java) - Contact book
- ✅ [`StandardFare`](../backend/src/main/java/com/bobcarrental/model/StandardFare.java) - Rate cards
- ✅ [`HeaderTemplate`](../backend/src/main/java/com/bobcarrental/model/HeaderTemplate.java) - Document templates

#### 3. Configuration
- ✅ Maven dependencies (Spring Boot 3.2.3, Java 21)
- ✅ H2 database configuration
- ✅ JWT settings
- ✅ Liquibase setup
- ✅ CORS configuration
- ✅ File upload settings
- ✅ Swagger/OpenAPI configuration

### ❌ Missing Components (Implementation Required)

1. **Repository Layer** - JPA repositories for all entities
2. **DTO Layer** - Request/Response DTOs with MapStruct mappers
3. **Security Layer** - JWT, UserDetailsService, SecurityConfig
4. **Service Layer** - Business logic and validations
5. **Controller Layer** - REST endpoints
6. **Database Migrations** - Liquibase changesets
7. **Migration Tools** - DBF to H2 data transfer
8. **Testing** - Unit and integration tests
9. **Frontend** - Angular application
10. **DevOps** - Docker and CI/CD

---

## 🏗️ Implementation Architecture

### Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Angular    │  │   Swagger    │  │   Mobile     │      │
│  │   Frontend   │  │      UI      │  │   (Future)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  REST Controllers (@RestController)                  │   │
│  │  - AuthController, ClientController, etc.            │   │
│  │  - Request validation (@Valid)                       │   │
│  │  - Response formatting (ResponseEntity)              │   │
│  │  - Exception handling (@ControllerAdvice)            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓ DTOs
┌─────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Business Logic (@Service)                           │   │
│  │  - ClientService, BookingService, etc.               │   │
│  │  - Validation (PresenceChk, CheckTime, etc.)         │   │
│  │  - Calculations (FareCalculationService)             │   │
│  │  - Transaction management (@Transactional)           │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓ Entities
┌─────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Data Access (JpaRepository)                         │   │
│  │  - ClientRepository, BookingRepository, etc.         │   │
│  │  - Custom queries (@Query)                           │   │
│  │  - Specifications (dynamic queries)                  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓ JPA/Hibernate
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  H2 In-Memory Database                               │   │
│  │  - Schema managed by Liquibase                       │   │
│  │  - Seed data from migrations                         │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Security Flow

```
┌──────────────┐
│   Client     │
│  (Browser/   │
│   Mobile)    │
└──────┬───────┘
       │ 1. POST /api/v1/auth/login
       │    {username, password}
       ↓
┌──────────────────────────────────────┐
│      AuthController                  │
│  - Validate credentials              │
│  - Generate JWT token                │
│  - Return token + user info          │
└──────┬───────────────────────────────┘
       │ 2. JWT Token
       ↓
┌──────────────┐
│   Client     │
│  Stores JWT  │
└──────┬───────┘
       │ 3. Subsequent requests
       │    Authorization: Bearer <token>
       ↓
┌──────────────────────────────────────┐
│   JwtAuthenticationFilter            │
│  - Extract token from header         │
│  - Validate token                    │
│  - Set SecurityContext               │
└──────┬───────────────────────────────┘
       │ 4. Authorized request
       ↓
┌──────────────────────────────────────┐
│   Controller with @PreAuthorize      │
│  - Check role (ADMIN/USER)           │
│  - Execute business logic            │
└──────────────────────────────────────┘
```

---

## 📝 Implementation Steps

### Phase 1: Repository Layer (Day 1-2)

Create JPA repositories for all entities with custom query methods.

#### File Structure
```
backend/src/main/java/com/bobcarrental/repository/
├── UserRepository.java
├── RoleRepository.java
├── ClientRepository.java
├── VehicleTypeRepository.java
├── VehicleImageRepository.java
├── BookingRepository.java
├── TripSheetRepository.java
├── BillingRepository.java
├── AccountRepository.java
├── AddressRepository.java
├── StandardFareRepository.java
└── HeaderTemplateRepository.java
```

#### Example: ClientRepository.java
```java
package com.bobcarrental.repository;

import com.bobcarrental.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    // Find by unique clientId (legacy key)
    Optional<Client> findByClientId(String clientId);
    
    // Check if clientId exists (for PresenceChk validation)
    boolean existsByClientId(String clientId);
    
    // Search by name (case-insensitive)
    List<Client> findByClientNameContainingIgnoreCase(String name);
    
    // Find by city
    List<Client> findByCity(String city);
    
    // Find clients with split rate enabled
    List<Client> findByIsSplitTrue();
    
    // Find tagged clients
    List<Client> findByTaggedTrue();
    
    // Custom query for advanced search
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.clientId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Client> searchClients(@Param("search") String search);
    
    // Count active clients
    @Query("SELECT COUNT(c) FROM Client c WHERE c.tagged = false")
    long countActiveClients();
}
```

#### Implementation Checklist
- [ ] Create all 12 repository interfaces
- [ ] Add custom query methods based on legacy system usage
- [ ] Add existence checks for validation (PresenceChk)
- [ ] Add search methods for filtering
- [ ] Add count methods for statistics

---

### Phase 2: DTO Layer (Day 3-5)

Create DTOs for request/response and MapStruct mappers for entity-DTO conversion.

#### File Structure
```
backend/src/main/java/com/bobcarrental/
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── ClientRequest.java
│   │   ├── BookingRequest.java
│   │   ├── TripSheetRequest.java
│   │   └── ...
│   ├── response/
│   │   ├── AuthResponse.java
│   │   ├── ClientResponse.java
│   │   ├── ClientSummaryResponse.java
│   │   ├── BookingResponse.java
│   │   └── ...
│   └── common/
│       ├── ApiResponse.java
│       ├── ErrorResponse.java
│       └── PageResponse.java
└── mapper/
    ├── ClientMapper.java
    ├── BookingMapper.java
    ├── TripSheetMapper.java
    └── ...
```

#### Example: ClientRequest.java
```java
package com.bobcarrental.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClientRequest {
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Client ID must be uppercase alphanumeric")
    private String clientId;
    
    @NotBlank(message = "Client name is required")
    @Size(max = 40, message = "Client name must not exceed 40 characters")
    private String clientName;
    
    @Size(max = 35, message = "Address1 must not exceed 35 characters")
    private String address1;
    
    @Size(max = 30, message = "Address2 must not exceed 30 characters")
    private String address2;
    
    @Size(max = 25, message = "Address3 must not exceed 25 characters")
    private String address3;
    
    @Size(max = 20, message = "Place must not exceed 20 characters")
    private String place;
    
    @Size(max = 15, message = "City must not exceed 15 characters")
    private String city;
    
    @Size(max = 6, message = "Pin code must not exceed 6 characters")
    private String pinCode;
    
    @Size(max = 25, message = "Phone must not exceed 25 characters")
    private String phone;
    
    @Size(max = 25, message = "Fax must not exceed 25 characters")
    private String fax;
    
    private String fare; // Memo field for custom pricing
    
    private Boolean isSplit = false;
    
    private Boolean tagged = false;
}
```

#### Example: ClientMapper.java
```java
package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.model.Client;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {
    
    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientRequest request);
    
    // Entity to Response (full details)
    ClientResponse toResponse(Client client);
    
    // Entity to Summary Response (for lists)
    @Mapping(target = "fullAddress", expression = "java(client.getFullAddress())")
    ClientSummaryResponse toSummaryResponse(Client client);
    
    // List conversions
    List<ClientResponse> toResponseList(List<Client> clients);
    List<ClientSummaryResponse> toSummaryResponseList(List<Client> clients);
    
    // Update entity from request (for PUT operations)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ClientRequest request, @MappingTarget Client client);
}
```

#### Implementation Checklist
- [ ] Create request DTOs with validation annotations
- [ ] Create response DTOs (full and summary versions)
- [ ] Create common DTOs (ApiResponse, ErrorResponse, PageResponse)
- [ ] Create MapStruct mappers for all entities
- [ ] Add custom mapping methods where needed
- [ ] Test DTO validation rules

---

### Phase 3: Security Infrastructure (Day 6-8)

Implement JWT authentication and role-based access control.

#### File Structure
```
backend/src/main/java/com/bobcarrental/security/
├── JwtTokenProvider.java
├── JwtAuthenticationFilter.java
├── CustomUserDetailsService.java
├── SecurityConfig.java
└── JwtAuthenticationEntryPoint.java
```

#### Example: JwtTokenProvider.java
```java
package com.bobcarrental.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(getSigningKey())
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

#### Implementation Checklist
- [ ] Create JwtTokenProvider for token generation/validation
- [ ] Create JwtAuthenticationFilter for request filtering
- [ ] Create CustomUserDetailsService for user loading
- [ ] Create SecurityConfig with security rules
- [ ] Create JwtAuthenticationEntryPoint for unauthorized access
- [ ] Configure CORS and CSRF
- [ ] Add @PreAuthorize annotations support

---

### Phase 4: Service Layer (Day 9-15)

Implement business logic, validations, and calculations.

#### File Structure
```
backend/src/main/java/com/bobcarrental/service/
├── AuthService.java
├── ClientService.java
├── VehicleTypeService.java
├── BookingService.java
├── TripSheetService.java
├── BillingService.java
├── AccountService.java
├── AddressService.java
├── StandardFareService.java
├── HeaderTemplateService.java
├── FareCalculationService.java
├── ValidationService.java
├── ReportService.java
└── impl/
    ├── AuthServiceImpl.java
    ├── ClientServiceImpl.java
    └── ...
```

#### Example: ClientService.java
```java
package com.bobcarrental.service;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    
    // CRUD operations
    ClientResponse createClient(ClientRequest request);
    ClientResponse updateClient(Long id, ClientRequest request);
    ClientResponse getClientById(Long id);
    ClientResponse getClientByClientId(String clientId);
    void deleteClient(Long id);
    
    // List and search
    Page<ClientSummaryResponse> getAllClients(Pageable pageable);
    List<ClientSummaryResponse> searchClients(String search);
    List<ClientSummaryResponse> getClientsByCity(String city);
    List<ClientSummaryResponse> getSplitRateClients();
    
    // Validation
    boolean existsByClientId(String clientId);
    void validateClientId(String clientId, Long excludeId);
    
    // Statistics
    long countActiveClients();
}
```

#### Example: FareCalculationService.java
```java
package com.bobcarrental.service;

import com.bobcarrental.model.StandardFare;
import com.bobcarrental.model.TripSheet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface FareCalculationService {
    
    /**
     * Calculate fare for FLAT rate (Status='F')
     * Based on hours and kilometers
     */
    Map<String, BigDecimal> calculateFlatRate(
        TripSheet tripSheet,
        StandardFare fare
    );
    
    /**
     * Calculate fare for SPLIT rate (Status='S')
     * Hiring + Fuel + Extra KM
     */
    Map<String, BigDecimal> calculateSplitRate(
        TripSheet tripSheet,
        StandardFare fare
    );
    
    /**
     * Calculate fare for OUTSTATION (Status='O')
     * Based on days, minimum KM, and night halts
     */
    Map<String, BigDecimal> calculateOutstationRate(
        TripSheet tripSheet,
        StandardFare fare
    );
    
    /**
     * Calculate total hours from start/end time and dates
     * Implements TIME2VAL logic from legacy system
     */
    int calculateTotalHours(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );
    
    /**
     * Calculate total days (calendar days)
     */
    int calculateTotalDays(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );
}
```

#### Implementation Checklist
- [ ] Create service interfaces for all entities
- [ ] Implement service classes with business logic
- [ ] Implement validation methods (PresenceChk, CheckTime, SuperCheckIt)
- [ ] Implement FareCalculationService with all algorithms
- [ ] Add transaction management (@Transactional)
- [ ] Add error handling and custom exceptions
- [ ] Implement ReportService for PDF generation

---

### Phase 5: Controller Layer (Day 16-20)

Create REST endpoints with proper request/response handling.

#### File Structure
```
backend/src/main/java/com/bobcarrental/controller/
├── AuthController.java
├── ClientController.java
├── VehicleTypeController.java
├── BookingController.java
├── TripSheetController.java
├── BillingController.java
├── AccountController.java
├── AddressController.java
├── StandardFareController.java
├── HeaderTemplateController.java
├── ReportController.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    ├── ValidationException.java
    └── UnauthorizedException.java
```

#### Example: ClientController.java
```java
package com.bobcarrental.controller;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "APIs for managing clients")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {
    
    private final ClientService clientService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create new client")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest request) {
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all clients with pagination")
    public ResponseEntity<Page<ClientSummaryResponse>> getAllClients(
            Pageable pageable) {
        Page<ClientSummaryResponse> clients = clientService.getAllClients(pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-client-id/{clientId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get client by legacy client ID")
    public ResponseEntity<ClientResponse> getClientByClientId(
            @PathVariable String clientId) {
        ClientResponse response = clientService.getClientByClientId(clientId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update client")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete client (ADMIN only)")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Search clients")
    public ResponseEntity<List<ClientSummaryResponse>> searchClients(
            @RequestParam String query) {
        List<ClientSummaryResponse> clients = clientService.searchClients(query);
        return ResponseEntity.ok(clients);
    }
}
```

#### Implementation Checklist
- [ ] Create all REST controllers
- [ ] Add proper HTTP methods (GET, POST, PUT, DELETE)
- [ ] Add validation (@Valid)
- [ ] Add security annotations (@PreAuthorize)
- [ ] Add Swagger documentation (@Operation, @Tag)
- [ ] Create GlobalExceptionHandler
- [ ] Add custom exception classes
- [ ] Test all endpoints with Postman

---

### Phase 6: Database Migrations (Day 21-23)

Set up Liquibase changesets for schema and seed data.

#### File Structure
```
backend/src/main/resources/db/changelog/
├── db.changelog-master.xml
├── changes/
│   ├── 001-create-users-roles-tables.xml
│   ├── 002-create-client-table.xml
│   ├── 003-create-vehicle-tables.xml
│   ├── 004-create-booking-table.xml
│   ├── 005-create-tripsheet-table.xml
│   ├── 006-create-billing-table.xml
│   ├── 007-create-account-table.xml
│   ├── 008-create-address-table.xml
│   ├── 009-create-fare-template-tables.xml
│   └── 010-seed-initial-data.xml
```

#### Example: db.changelog-master.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <include file="db/changelog/changes/001-create-users-roles-tables.xml"/>
    <include file="db/changelog/changes/002-create-client-table.xml"/>
    <include file="db/changelog/changes/003-create-vehicle-tables.xml"/>
    <include file="db/changelog/changes/004-create-booking-table.xml"/>
    <include file="db/changelog/changes/005-create-tripsheet-table.xml"/>
    <include file="db/changelog/changes/006-create-billing-table.xml"/>
    <include file="db/changelog/changes/007-create-account-table.xml"/>
    <include file="db/changelog/changes/008-create-address-table.xml"/>
    <include file="db/changelog/changes/009-create-fare-template-tables.xml"/>
    <include file="db/changelog/changes/010-seed-initial-data.xml"/>
</databaseChangeLog>
```

#### Implementation Checklist
- [ ] Create master changelog file
- [ ] Create changeset for each table
- [ ] Add indexes for foreign keys
- [ ] Add unique constraints
- [ ] Create seed data changeset (admin/user accounts)
- [ ] Test migrations with clean database
- [ ] Add rollback scripts

---

### Phase 7: Testing (Day 24-30)

Create comprehensive unit and integration tests.

#### File Structure
```
backend/src/test/java/com/bobcarrental/
├── repository/
│   ├── ClientRepositoryTest.java
│   └── ...
├── service/
│   ├── ClientServiceTest.java
│   ├── FareCalculationServiceTest.java
│   └── ...
├── controller/
│   ├── ClientControllerTest.java
│   └── ...
└── integration/
    ├── ClientIntegrationTest.java
    └── ...
```

#### Implementation Checklist
- [ ] Create repository tests
- [ ] Create service unit tests with Mockito
- [ ] Create controller tests with MockMvc
- [ ] Create integration tests with @SpringBootTest
- [ ] Test security (JWT, roles)
- [ ] Test validation rules
- [ ] Test fare calculations
- [ ] Achieve 80%+ code coverage

---

### Phase 8: Frontend (Day 31-50)

Create Angular application with all modules.

#### File Structure
```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── auth/
│   │   │   ├── guards/
│   │   │   ├── interceptors/
│   │   │   └── services/
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   ├── directives/
│   │   │   └── pipes/
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   ├── clients/
│   │   │   ├── vehicles/
│   │   │   ├── bookings/
│   │   │   ├── tripsheets/
│   │   │   ├── billing/
│   │   │   ├── accounts/
│   │   │   ├── addresses/
│   │   │   ├── fares/
│   │   │   └── templates/
│   │   └── app.component.ts
│   └── environments/
└── package.json
```

#### Implementation Checklist
- [ ] Initialize Angular project
- [ ] Set up Angular Material
- [ ] Create authentication module
- [ ] Create JWT interceptor
- [ ] Create route guards
- [ ] Create all feature modules
- [ ] Create shared components
- [ ] Implement responsive design
- [ ] Add form validation
- [ ] Create E2E tests with Playwright

---

### Phase 9: DevOps (Day 51-55)

Set up Docker and deployment configuration.

#### Implementation Checklist
- [ ] Create Dockerfile for backend
- [ ] Create Dockerfile for frontend
- [ ] Create docker-compose.yml
- [ ] Set up environment variables
- [ ] Create CI/CD pipeline (GitHub Actions)
- [ ] Add health checks
- [ ] Configure logging
- [ ] Create deployment documentation

---

## 🎯 Success Metrics

- ✅ All 14 entities with repositories
- ✅ Complete DTO layer with MapStruct
- ✅ JWT authentication working
- ✅ All CRUD operations functional
- ✅ Fare calculations accurate
- ✅ 80%+ test coverage
- ✅ Responsive Angular UI
- ✅ Docker deployment ready
- ✅ API documentation complete
- ✅ Zero critical bugs

---

## 📅 Timeline Summary

| Phase | Duration | Components |
|-------|----------|------------|
| Repository Layer | 2 days | 12 repositories |
| DTO Layer | 3 days | DTOs + Mappers |
| Security | 3 days | JWT + RBAC |
| Service Layer | 7 days | Business logic |
| Controller Layer | 5 days | REST APIs |
| Database | 3 days | Liquibase |
| Testing | 7 days | Unit + Integration |
| Frontend | 20 days | Angular app |
| DevOps | 5 days | Docker + CI/CD |
| **Total** | **55 days** | **~2 months** |

---

## 🚀 Next Steps

1. **Review this plan** with stakeholders
2. **Start with Phase 1** - Repository layer
3. **Switch to Code mode** for implementation
4. **Follow the checklist** for each phase
5. **Test continuously** as you build
6. **Document as you go**

---

**Document Version**: 1.0  
**Last Updated**: 2026-03-26  
**Author**: Bob (AI Software Engineer)  
**Status**: Ready for Implementation