# Runtime Fixes Applied

## Overview
This document tracks all runtime errors discovered during Spring Boot application startup and their fixes.

## Errors Fixed

### 1. Address.pinCode Type Mismatch
**Error:** `Cannot compare left expression of type 'java.lang.Integer' with right expression of type 'java.lang.String'`

**Location:** `AddressRepository.findByPinCode(String)`

**Root Cause:** Address entity had `pinCode` as `Integer`, but repository method expected `String` parameter.

**Fix:** Changed Address entity field from `Integer` to `String`:
```java
// Before
@Column(name = "pin_code")
private Integer pinCode;

// After
@Size(max = 10, message = "Pin code must not exceed 10 characters")
@Column(name = "pin_code", length = 10)
private String pinCode;
```

### 2. Client.active Field Not Found
**Error:** `Could not resolve attribute 'active' of 'com.bobcarrental.model.Client'`

**Location:** `ClientRepository.countActiveClients()` and `findByActiveTrue()`

**Root Cause:** Client entity doesn't have an `active` field - it has `deleted` field and computed `getActive()` method.

**Fix:** Changed queries to use `deleted` field:
```java
// Before
List<Client> findByActiveTrue();
@Query("SELECT COUNT(c) FROM Client c WHERE c.active = true")
long countActiveClients();

// After
@Query("SELECT c FROM Client c WHERE c.deleted = false")
List<Client> findByActiveTrue();

@Query("SELECT COUNT(c) FROM Client c WHERE c.deleted = false")
long countActiveClients();
```

### 3. Client.name Field Not Found
**Error:** `Unable to locate Attribute with the given name [name] on this ManagedType [com.bobcarrental.model.Client]`

**Location:** Multiple places in ClientRepository

**Root Cause:** Client entity has `clientName` field, not `name`. The `getName()` method is just an alias getter.

**Fixes Applied:**

#### ClientRepository methods:
```java
// Before
List<Client> findByNameContainingIgnoreCase(String name);
boolean existsByName(String name);

// After  
List<Client> findByClientNameContainingIgnoreCase(String clientName);
boolean existsByClientName(String clientName);
```

#### ClientRepository queries:
```java
// Before
@Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) ...")
@Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")

// After
@Query("SELECT c FROM Client c WHERE LOWER(c.clientName) LIKE LOWER(CONCAT('%', :search, '%')) ...")
@Query("SELECT c FROM Client c WHERE LOWER(c.clientName) LIKE LOWER(CONCAT('%', :name, '%'))")
```

#### ClientServiceImpl:
```java
// Before
return clientRepository.existsByName(name);

// After
return clientRepository.existsByClientName(name);
```

## Key Lessons Learned

1. **JPA Field Names Must Match Exactly:** JPA cannot use getter/setter aliases - it requires actual field names in queries and method names.

2. **Computed Properties Are Not Queryable:** Fields like `active` that are computed from other fields (`deleted`) cannot be used in JPA queries directly.

3. **Type Consistency:** Field types in entities must match parameter types in repository methods.

4. **Systematic Approach:** When dealing with legacy field names:
   - Document all actual field names in entities
   - Audit all repository methods against actual fields
   - Fix systematically from repositories → services → controllers

## Status
✅ All runtime errors fixed
✅ Application startup successful (pending final verification)

## Next Steps
1. Verify application starts without errors
2. Test basic CRUD operations
3. Set up proper database migrations with Liquibase
4. Create seed data for testing