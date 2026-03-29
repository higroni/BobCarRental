# Complete Module Implementation Template - Client Module

Ovaj dokument sadrži **kompletnu implementaciju Client modula** od Repository do Controller. Kopirajte ovaj pattern za sve ostale module.

---

## 📦 1. Repository Layer ✅

**Fajl**: `ClientRepository.java` (već kreiran)

```java
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);
    boolean existsByClientId(String clientId);
    List<Client> searchClients(String search);
    // ... ostale metode
}
```

---

## 📋 2. DTO Layer ✅

### Request DTO
**Fajl**: `ClientRequest.java` (već kreiran)

### Response DTOs
**Fajlovi**: `ClientResponse.java`, `ClientSummaryResponse.java` (već kreirani)

### Mapper
**Fajl**: `ClientMapper.java` (već kreiran)

---

## 🔧 3. Service Layer

### Interface: `ClientService.java`

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
    
    // Validation
    void validateClientId(String clientId, Long excludeId);
    
    // Statistics
    long countActiveClients();
}
```

### Implementation: `ClientServiceImpl.java`

```java
package com.bobcarrental.service.impl;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import com.bobcarrental.mapper.ClientMapper;
import com.bobcarrental.model.Client;
import com.bobcarrental.repository.ClientRepository;
import com.bobcarrental.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.debug("Creating new client with clientId: {}", request.getClientId());
        
        // Validation: PresenceChk - Check if clientId already exists
        validateClientId(request.getClientId(), null);
        
        // Map and save
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);
        
        log.info("Client created successfully with id: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }
    
    @Override
    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        log.debug("Updating client with id: {}", id);
        
        // Find existing client
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        
        // Validation: Check if new clientId is unique (if changed)
        if (!existingClient.getClientId().equals(request.getClientId())) {
            validateClientId(request.getClientId(), id);
        }
        
        // Update and save
        clientMapper.updateEntityFromRequest(request, existingClient);
        Client updatedClient = clientRepository.save(existingClient);
        
        log.info("Client updated successfully with id: {}", id);
        return clientMapper.toResponse(updatedClient);
    }
    
    @Override
    public ClientResponse getClientById(Long id) {
        log.debug("Fetching client by id: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        
        return clientMapper.toResponse(client);
    }
    
    @Override
    public ClientResponse getClientByClientId(String clientId) {
        log.debug("Fetching client by clientId: {}", clientId);
        
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with clientId: " + clientId));
        
        return clientMapper.toResponse(client);
    }
    
    @Override
    @Transactional
    public void deleteClient(Long id) {
        log.debug("Deleting client with id: {}", id);
        
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        
        // Note: In legacy system, clients cannot be deleted
        // Consider soft delete or throw exception
        clientRepository.deleteById(id);
        
        log.info("Client deleted successfully with id: {}", id);
    }
    
    @Override
    public Page<ClientSummaryResponse> getAllClients(Pageable pageable) {
        log.debug("Fetching all clients with pagination");
        
        Page<Client> clientPage = clientRepository.findAll(pageable);
        return clientPage.map(clientMapper::toSummaryResponse);
    }
    
    @Override
    public List<ClientSummaryResponse> searchClients(String search) {
        log.debug("Searching clients with query: {}", search);
        
        List<Client> clients = clientRepository.searchClients(search);
        return clientMapper.toSummaryResponseList(clients);
    }
    
    @Override
    public List<ClientSummaryResponse> getClientsByCity(String city) {
        log.debug("Fetching clients by city: {}", city);
        
        List<Client> clients = clientRepository.findByCity(city);
        return clientMapper.toSummaryResponseList(clients);
    }
    
    @Override
    public void validateClientId(String clientId, Long excludeId) {
        // PresenceChk validation from legacy system
        boolean exists = clientRepository.existsByClientId(clientId);
        
        if (exists) {
            // If updating, check if it's the same client
            if (excludeId != null) {
                Client existingClient = clientRepository.findByClientId(clientId).orElse(null);
                if (existingClient != null && !existingClient.getId().equals(excludeId)) {
                    throw new ValidationException("Client ID already exists: " + clientId);
                }
            } else {
                throw new ValidationException("Client ID already exists: " + clientId);
            }
        }
    }
    
    @Override
    public long countActiveClients() {
        return clientRepository.countActiveClients();
    }
}
```

---

## 🎮 4. Controller Layer

### `ClientController.java`

```java
package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "Create new client", description = "Creates a new client in the system")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @Valid @RequestBody ClientRequest request) {
        
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client created successfully", response));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all clients", description = "Retrieves all clients with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ClientSummaryResponse>>> getAllClients(
            @PageableDefault(size = 20, sort = "clientName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<ClientSummaryResponse> clientPage = clientService.getAllClients(pageable);
        PageResponse<ClientSummaryResponse> pageResponse = PageResponse.of(clientPage);
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get client by ID", description = "Retrieves a client by its database ID")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable Long id) {
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/by-client-id/{clientId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get client by legacy client ID", description = "Retrieves a client by its legacy client ID")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientByClientId(@PathVariable String clientId) {
        ClientResponse response = clientService.getClientByClientId(clientId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update client", description = "Updates an existing client")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success("Client updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete client", description = "Deletes a client (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Client deleted successfully", null));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Search clients", description = "Searches clients by name, ID, city, or phone")
    public ResponseEntity<ApiResponse<List<ClientSummaryResponse>>> searchClients(
            @RequestParam String query) {
        
        List<ClientSummaryResponse> clients = clientService.searchClients(query);
        return ResponseEntity.ok(ApiResponse.success(clients));
    }
    
    @GetMapping("/by-city/{city}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get clients by city", description = "Retrieves all clients from a specific city")
    public ResponseEntity<ApiResponse<List<ClientSummaryResponse>>> getClientsByCity(@PathVariable String city) {
        List<ClientSummaryResponse> clients = clientService.getClientsByCity(city);
        return ResponseEntity.ok(ApiResponse.success(clients));
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Count active clients", description = "Returns the count of active clients")
    public ResponseEntity<ApiResponse<Long>> countActiveClients() {
        long count = clientService.countActiveClients();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
```

---

## 🚨 5. Exception Handling

### Custom Exceptions

```java
// ResourceNotFoundException.java
package com.bobcarrental.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// ValidationException.java
package com.bobcarrental.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
// GlobalExceptionHandler.java
package com.bobcarrental.controller.exception;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.ErrorDetails;
import com.bobcarrental.dto.common.FieldError;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorDetails error = ErrorDetails.of("NOT_FOUND", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), error));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        ErrorDetails error = ErrorDetails.of("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), error));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors().stream()
                .map(error -> FieldError.of(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());
        
        ErrorDetails error = ErrorDetails.withFieldErrors(
                "VALIDATION_ERROR",
                "Invalid input data",
                fieldErrors
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", error));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        
        ErrorDetails error = ErrorDetails.of("ACCESS_DENIED", "You don't have permission to access this resource");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied", error));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        ErrorDetails error = ErrorDetails.of("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", error));
    }
}
```

---

## 📝 Kako Koristiti Ovaj Template

### Za Svaki Novi Modul:

1. **Kopirajte Service interfejs i implementaciju**
   - Zamenite `Client` sa `{YourEntity}`
   - Prilagodite metode prema potrebama entiteta

2. **Kopirajte Controller**
   - Zamenite `Client` sa `{YourEntity}`
   - Prilagodite endpoints
   - Dodajte specifične metode ako je potrebno

3. **Kreirajte Exception klase** (ako već ne postoje)

4. **Testirajte** svaki endpoint

---

## 🎯 Primer za Booking Modul

```java
// BookingService.java
public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse updateBooking(Long id, BookingRequest request);
    BookingResponse getBookingById(Long id);
    void deleteBooking(Long id);
    Page<BookingSummaryResponse> getAllBookings(Pageable pageable);
    List<BookingSummaryResponse> getBookingsByDate(LocalDate date);
    List<BookingSummaryResponse> getTodaysBookings();
}

// BookingServiceImpl.java - Ista struktura kao ClientServiceImpl
// BookingController.java - Ista struktura kao ClientController
```

---

## ✅ Checklist za Svaki Modul

- [ ] Repository (već kreiran)
- [ ] DTOs (Request, Response, Summary)
- [ ] Mapper (MapStruct)
- [ ] Service interface
- [ ] Service implementation
- [ ] Controller
- [ ] Exception handling (globalno)
- [ ] Swagger dokumentacija (@Operation, @Tag)
- [ ] Security (@PreAuthorize)
- [ ] Validacija (@Valid)
- [ ] Logging (log.debug, log.info, log.error)
- [ ] Transaction management (@Transactional)

---

**Ovaj template pokriva kompletan flow od baze do API-ja!**
Kopirajte ga za sve ostale module (Booking, TripSheet, Billing, itd.)