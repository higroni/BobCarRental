# Migration Fix Instructions

## Summary of Issues Fixed

### 1. ✅ TripSheet Service - Optional Booking
**Problem:** Service tried to find Booking with null ID  
**Fix:** Added null check in `TripSheetServiceImpl.createTripSheet()`  
**Status:** Code fixed and recompiled

### 2. ⚠️ Billing Table - NULL Constraints
**Problem:** Database columns `trp_num` and `bill_amt` don't allow NULL  
**Fix:** SQL script created to alter columns  
**Status:** Needs to be executed

### 3. ✅ Field Name Mappings
**Problem:** Migration sent wrong field names (startDt vs startDate)  
**Fix:** Updated migration script mappings  
**Status:** Fixed in migrate_dbf_to_h2.py

---

## Steps to Complete Migration

### Option A: Fix Database Schema (Recommended)

#### Step 1: Stop Spring Boot Application
```bash
cd bobcarrental/backend
.\stop.bat
```

#### Step 2: Run Database Fix Script
```bash
cd bobcarrental/backend
.\fix-database.bat
```

This will:
- Connect to H2 database
- Alter `billings.trp_num` to allow NULL
- Alter `billings.bill_amt` to allow NULL
- Verify changes

#### Step 3: Restart Spring Boot Application
```bash
cd bobcarrental/backend
mvn spring-boot:run
```

Wait for "Started BobCarRentalApplication" message.

#### Step 4: Run Migration
```bash
cd bobcarrental/migration
python migrate_dbf_to_h2.py
```

---

### Option B: Clean Database Restart (Alternative)

If Option A fails, you can start fresh:

#### Step 1: Stop Application
```bash
cd bobcarrental/backend
.\stop.bat
```

#### Step 2: Delete Database
```bash
cd bobcarrental/backend
del data\bobcarrental.mv.db
```

#### Step 3: Restart Application
```bash
mvn spring-boot:run
```

Hibernate will recreate database with correct schema (NULL allowed).

#### Step 4: Run Migration
```bash
cd bobcarrental/migration
python migrate_dbf_to_h2.py
```

---

## Expected Migration Results

After fixes, you should see:

### ✅ Successful Migrations
- **ADDRESS.TRP**: 1/1 records (already working)
- **TRPSHEET.TRP**: 1/1 records (will work after fixes)
- **BILLING.TRP**: 1/1 records (will work after fixes)

### ⚠️ Seed Data Conflicts (Expected)
- **VEHTYPE.TRP**: 0/4 new (all exist as seed data)
- **CLIENT.TRP**: 0/2 new (all exist as seed data)
- **BOOKING.TRP**: 0/2 new (all exist as seed data)

These are **not errors** - the records already exist from seed data.

---

## Verification

### Check TripSheet Created
```bash
curl -X GET "http://localhost:8080/api/v1/trip-sheets/1" ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

Expected: 200 OK with trip sheet data

### Check Billing Created
```bash
curl -X GET "http://localhost:8080/api/v1/billings/1" ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

Expected: 200 OK with billing data

---

## Troubleshooting

### Database Fix Script Fails
**Error:** "Cannot connect to database"  
**Solution:** Make sure application is stopped first

**Error:** "H2 jar not found"  
**Solution:** Run `mvn dependency:resolve` first

### Migration Still Fails
1. Check backend logs for detailed error messages
2. Verify Spring Boot is running: `http://localhost:8080/actuator/health`
3. Test authentication: Run `test-auth.bat`
4. Check database schema:
   ```sql
   SELECT COLUMN_NAME, IS_NULLABLE 
   FROM INFORMATION_SCHEMA.COLUMNS 
   WHERE TABLE_NAME = 'BILLINGS';
   ```

### VehicleType First Record 500 Error
If you still see 500 error for first VehicleType record:
1. Check backend console for full stack trace
2. It might be trying to create duplicate (AMB already exists)
3. This is expected if seed data exists

---

## Files Created/Modified

### Backend Code Changes
- ✅ `TripSheetServiceImpl.java` - Added null check for optional bookingId
- ✅ `Billing.java` - Removed @NotNull from trpNum and billAmt
- ✅ `BillingServiceImpl.java` - Added null check for optional TripSheet

### Migration Script Changes
- ✅ `migrate_dbf_to_h2.py` - Fixed field mappings (startDate, endDate, startTime, endTime, billAmount)

### Database Scripts
- 📄 `fix-database-schema.sql` - SQL to alter billings table
- 📄 `fix-database.bat` - Batch script to execute SQL

### Documentation
- 📄 `ERROR_ANALYSIS.md` - Detailed error analysis
- 📄 `MIGRATION_STATUS.md` - Current migration status
- 📄 `FIX_AND_RETRY.md` - This file

---

## Next Steps After Successful Migration

1. ✅ Verify all data migrated correctly
2. 📝 Test FARES.TXT and HEADER.TXT parser
3. 🧪 Regenerate unit tests with correct field names
4. 📊 Implement PDF report generation
5. 🎨 Start Angular frontend development

---

## Quick Reference

### Start Backend
```bash
cd bobcarrental/backend
mvn spring-boot:run
```

### Stop Backend
```bash
cd bobcarrental/backend
.\stop.bat
```

### Run Migration
```bash
cd bobcarrental/migration
python migrate_dbf_to_h2.py
```

### Fix Database
```bash
cd bobcarrental/backend
.\stop.bat
.\fix-database.bat
mvn spring-boot:run
```

---

**Status:** Ready to execute database fix and retry migration  
**Last Updated:** 2026-03-28  
**Backend Compilation:** ✅ BUILD SUCCESS (110 files)