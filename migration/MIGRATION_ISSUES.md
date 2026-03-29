# DBF Migration Issues and Solutions

## Summary of Migration Test Run (2026-03-28)

### ✅ Successfully Fixed
1. **Field Mappings** - All DBF field names corrected to match actual DBF structure
2. **ADDRESS mapping** - Changed `contact` to `name`
3. **BILLING mapping** - Changed `amount` to `billAmount`

### ⚠️ Issues Found

#### 1. VEHTYPE.TRP - First Record 500 Error
**Status:** Needs investigation
**Error:** Internal server error on first record (AMB - AMBASSADOR)
**Note:** Records 2-4 failed because they already exist (expected)

#### 2. CLIENT.TRP - Already Exists
**Status:** Expected behavior
**Records:** VAT, CLN already in database from seed data
**Action:** None needed

#### 3. ADDRESS.TRP - Validation Error
**Status:** ✅ FIXED
**Error:** "Name is required" - was sending `contact` instead of `name`
**Fix:** Updated mapping from `NAME: 'contact'` to `NAME: 'name'`

#### 4. BOOKING.TRP - Already Exists
**Status:** Expected behavior
**Records:** 2001-03-01, 2026-03-26 already in database
**Action:** None needed

#### 5. TRPSHEET.TRP - Validation Error
**Status:** ❌ NEEDS FIX
**Error:** `@NotBlank` constraint on `Long` type field `bookingId`
**Root Cause:** TripSheetRequest DTO has incorrect validation annotation
**Solution:** Change `@NotBlank` to `@NotNull` for Long fields

**File:** `bobcarrental/backend/src/main/java/com/bobcarrental/dto/request/TripSheetRequest.java`
```java
// WRONG:
@NotBlank(message = "Booking ID is required")
private Long bookingId;

// CORRECT:
@NotNull(message = "Booking ID is required")
private Long bookingId;
```

#### 6. BILLING.TRP - Resource Not Found
**Status:** Expected behavior (data issue)
**Error:** TripSheet not found with id: 'null'
**Root Cause:** DBF record has `trpNum` = null
**Note:** This is valid - billing can exist without trip sheet in legacy system
**Solution:** Make `trpNum` optional in BillingRequest validation

## Next Steps

1. ✅ Fix TripSheetRequest validation (`@NotBlank` → `@NotNull` for Long fields)
2. ✅ Make `trpNum` optional in BillingRequest
3. ✅ Investigate VEHTYPE first record 500 error
4. ✅ Re-run migration after fixes
5. ✅ Test with fresh database (delete existing records)

## Migration Statistics (Current Run)

| File | Total | Success | Failed | Skipped | Notes |
|------|-------|---------|--------|---------|-------|
| VEHTYPE.TRP | 4 | 0 | 4 | 0 | 1 x 500 error, 3 x already exist |
| CLIENT.TRP | 2 | 0 | 2 | 0 | Already exist |
| ADDRESS.TRP | 1 | 0 | 1 | 0 | Fixed: name field |
| BOOKING.TRP | 2 | 0 | 2 | 0 | Already exist |
| TRPSHEET.TRP | 1 | 0 | 1 | 0 | Validation error |
| BILLING.TRP | 1 | 0 | 1 | 0 | TripSheet not found |
| **TOTAL** | **11** | **0** | **11** | **0** | |

## Field Mappings (Corrected)

### VEHTYPE.TRP
```python
'TYPEID': 'typeId',
'TYPEDESC': 'typeName',
'TAGGED': 'tagged'
```

### CLIENT.TRP
```python
'CLIENTID': 'clientId',
'CLIENTNAME': 'clientName',
'ADDRESS1': 'address1',
'ADDRESS2': 'address2',
'ADDRESS3': 'address3',
'PLACE': 'place',
'PINCODE': 'pin',
'PHONE': 'phone1',
'FAX': 'fax',
'TAGGED': 'tagged'
```

### ADDRESS.TRP
```python
'CLIENTID': 'clientId',
'NAME': 'name',  # Fixed: was 'contact'
'ADDRESS1': 'address1',
'ADDRESS2': 'address2',
'ADDRESS3': 'address3',
'PLACE': 'place',
'PHONE': 'phone',
'TAGGED': 'tagged'
```

### BOOKING.TRP
```python
'CLIENTID': 'clientId',
'TYPEID': 'typeId',
'BOOKDATE': 'bookDate',
'TIME': 'time',
'INFO1': 'info1',
'INFO2': 'info2',
'INFO3': 'info3',
'INFO4': 'info4',
'REF': 'ref',
'TODAYDATE': 'todayDate',
'TAGGED': 'tagged'
```

### TRPSHEET.TRP
```python
'TRPNUM': 'trpNum',
'TRPDATE': 'trpDate',
'CLIENTID': 'clientId',
'CLIENTNAME': 'clientName',
'TYPEID': 'typeId',
'REGNUM': 'regNum',
'DRIVER': 'driver',
'STARTDT': 'startDt',
'STARTTM': 'startTm',
'STARTKM': 'startKm',
'ENDDT': 'endDt',
'ENDTM': 'endTm',
'ENDKM': 'endKm',
'DAYS': 'days',
'TIME': 'time',
'HIRING': 'hiring',
'EXTRA': 'extra',
'HALT': 'halt',
'PERMIT': 'permit',
'MISC': 'misc',
'MINIMUM': 'minimum',
'STATUS': 'status',
'ISBILLED': 'isBilled',
'BILLNUM': 'billNum',
'BILLDATE': 'billDate',
'TAGGED': 'tagged'
```

### BILLING.TRP
```python
'BILLNUM': 'billNum',
'BILLDATE': 'billDate',
'CLIENTID': 'clientId',
'BILLAMT': 'billAmount',  # Fixed: was 'amount'
'TAGGED': 'tagged'