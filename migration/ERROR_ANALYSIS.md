# Migration Error Analysis - 500 Errors

## Date: 2026-03-28

## Errors Identified from Backend Logs

### 1. TripSheet 500 Error ❌
**Error Message:**
```
java.lang.IllegalArgumentException: The given id must not be null
at org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById
```

**Root Cause:**
- TripSheetServiceImpl tries to find Booking by `bookingId`
- Migration sends `bookingId: null` because legacy system doesn't have mandatory Booking relationship
- Service calls `bookingRepository.findById(null)` which throws IllegalArgumentException

**Location:** `TripSheetServiceImpl.createTripSheet()` line ~45

**Solution:**
Add null check before calling `findById()`:
```java
if (request.getBookingId() != null) {
    Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    tripSheet.setBooking(booking);
}
```

---

### 2. Billing 500 Error ❌
**Error Message:**
```
NULL not allowed for column "TRP_NUM"
org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
```

**Root Cause:**
- Database column `trp_num` has NOT NULL constraint
- We removed `@NotNull` from Billing entity but database schema wasn't updated
- Migration sends `trpNum: null` because legacy billing can exist without trip sheet
- Hibernate tries to insert NULL but database rejects it

**Solution Options:**

#### Option A: Alter Database Column (Recommended)
Create SQL script to modify column:
```sql
ALTER TABLE billings ALTER COLUMN trp_num INTEGER NULL;
```

#### Option B: Drop and Recreate Database
1. Stop Spring Boot application
2. Delete H2 database file: `bobcarrental/backend/data/bobcarrental.mv.db`
3. Restart application (Hibernate will recreate with correct schema)

#### Option C: Use Liquibase Migration
Create proper migration script to alter column constraint.

---

### 3. VehicleType First Record 500 Error ⚠️
**Status:** Not enough information in logs provided

**Hypothesis:**
- First record (AMB) gives 500 error
- Other records give 400 "already exists" (expected - seed data)
- Possible causes:
  - Similar null ID issue
  - Foreign key constraint violation
  - Unique constraint violation on different field

**Need:** Full stack trace for VehicleType 500 error

---

## Recommended Fix Order

### Step 1: Fix TripSheet Service (Code Change)
File: `TripSheetServiceImpl.java`

Add null check for optional bookingId:
```java
// Around line 45
if (request.getBookingId() != null) {
    Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Booking not found with id: " + request.getBookingId()));
    tripSheet.setBooking(booking);
}
```

### Step 2: Fix Billing Database Schema (Database Change)
**Option A - SQL Script (Fastest):**
```sql
-- Run this in H2 console or via script
ALTER TABLE billings ALTER COLUMN trp_num INTEGER NULL;
ALTER TABLE billings ALTER COLUMN bill_amt DECIMAL(10,2) NULL;
```

**Option B - Drop Database (Clean Start):**
```bash
# Stop application
cd bobcarrental/backend
.\stop.bat

# Delete database
del data\bobcarrental.mv.db

# Restart application
mvn spring-boot:run
```

### Step 3: Recompile and Restart
```bash
cd bobcarrental/backend
mvn clean compile -DskipTests
mvn spring-boot:run
```

### Step 4: Rerun Migration
```bash
cd bobcarrental/migration
python migrate_dbf_to_h2.py
```

---

## Additional Fixes Needed

### Make bookingId Optional in TripSheetRequest
Currently `bookingId` is not validated, which is correct. Keep it as `private Long bookingId;` without `@NotNull`.

### Verify Client "MISC" Exists
All test data references `clientId: "MISC"`. Ensure this client exists in database (should be from seed data).

### Verify VehicleType "AMB" Exists
Test data references `typeId: "AMB"`. Ensure this vehicle type exists.

---

## Testing After Fixes

### Test 1: TripSheet Creation
```json
{
  "trpNum": 1,
  "trpDate": "2001-03-01",
  "clientId": "MISC",
  "typeId": "AMB",
  "regNum": "9191",
  "driver": "MUNNUSWAMY",
  "startDate": "2001-03-01",
  "startTime": "09:00",
  "startKm": 1,
  "endDate": "2001-03-01",
  "endTime": "10:00",
  "endKm": 10000,
  "bookingId": null
}
```
Expected: 201 Created

### Test 2: Billing Creation
```json
{
  "billNum": 1,
  "billDate": "2001-03-01",
  "clientId": "MISC",
  "billAmount": 40043.5,
  "trpNum": null
}
```
Expected: 201 Created

---

## Summary

| Issue | Type | Status | Fix Required |
|-------|------|--------|--------------|
| TripSheet bookingId null | Code | ❌ | Add null check in service |
| Billing trpNum NOT NULL | Database | ❌ | Alter column or drop DB |
| Billing billAmt NOT NULL | Database | ❌ | Alter column or drop DB |
| VehicleType first record | Unknown | ⚠️ | Need more logs |

**Next Action:** Fix TripSheetServiceImpl and alter database schema, then retest migration.