# TripSheet to Billing Workflow Guide

## Overview
This guide explains how to properly create and bill trip sheets in the Bob Car Rental System.

## Trip Sheet Status Flow

Trip sheets go through the following statuses:
1. **PENDING** - Initial state when trip sheet is created
2. **IN_PROGRESS** - Trip is currently ongoing
3. **FINISHED** - Trip is completed (required for billing)
4. **CANCELLED** - Trip was cancelled

## How to Create and Bill a Trip Sheet

### Step 1: Create a Trip Sheet

1. Navigate to **Trip Sheets** module
2. Click **"New Trip Sheet"**
3. Fill in required fields:
   - Trip Number (unique)
   - Trip Date
   - Vehicle Registration Number
   - Client
   - Vehicle Type
   - Start/End KM
   - Start/End Date & Time
   - Fare amounts
4. Set **Status** to **"Pending"** (default)
5. Click **Save**

### Step 2: Mark Trip as Finished

Once the trip is completed:

1. Go to **Trip Sheets** list
2. Find your trip sheet
3. Click **Edit** (pencil icon)
4. Change **Status** dropdown to **"Finished"**
5. Verify all fare amounts are correct
6. Click **Update**

### Step 3: Create Billing

Now you can bill the finished trip:

1. Navigate to **Billings** module
2. Click **"New Billing"**
3. Fill in required fields:
   - Bill Number (unique)
   - Bill Date
   - Client (same as trip sheet)
   - Bill Amount (total from trip sheet)
4. **IMPORTANT:** Enter the **Trip Number** from Step 1
5. Click **Save**

✅ Billing will be created successfully!

## Alternative: Manual Billing (Without Trip Sheet)

If you want to create a billing without linking to a trip sheet:

1. Navigate to **Billings** module
2. Click **"New Billing"**
3. Fill in required fields:
   - Bill Number
   - Bill Date
   - Client
   - Bill Amount
4. **Leave "Trip Number" field EMPTY**
5. Click **Save**

✅ Manual billing will be created!

## Common Errors and Solutions

### Error: "Cannot create billing for non-finished trip sheet"

**Cause:** You're trying to bill a trip sheet that is not marked as FINISHED.

**Solution:**
1. Go to Trip Sheets
2. Edit the trip sheet
3. Change Status to "Finished"
4. Save
5. Try billing again

### Error: "Bill number already exists"

**Cause:** The bill number you entered is already used.

**Solution:**
- Use a different, unique bill number

### Error: "TripSheet not found"

**Cause:** The trip number you entered doesn't exist.

**Solution:**
- Verify the trip number exists in Trip Sheets list
- Check for typos in the trip number

## Status Indicators

### In Trip Sheet List:
- Status column shows current trip status
- Only "FINISHED" trips can be billed

### In Billing List:
- Status shows "Paid" or "Unpaid" based on balance
- Balance = Total Amount - Paid Amount

## Best Practices

1. **Always complete trip sheets before billing**
   - Fill in all fare amounts
   - Verify KM readings
   - Set status to FINISHED

2. **Use sequential bill numbers**
   - Makes tracking easier
   - Follows accounting standards

3. **Link billings to trip sheets when possible**
   - Maintains audit trail
   - Easier reporting

4. **Review before saving**
   - Check all amounts
   - Verify client information
   - Confirm trip number is correct

## Quick Reference

| Action | Module | Status Required |
|--------|--------|-----------------|
| Create Trip Sheet | Trip Sheets | Any |
| Edit Trip Sheet | Trip Sheets | Any |
| Mark as Finished | Trip Sheets | Change to FINISHED |
| Bill Trip Sheet | Billings | Trip must be FINISHED |
| Manual Billing | Billings | N/A (no trip number) |

## Example Workflow

```
1. Create Trip Sheet #101
   Status: PENDING
   Client: DEMO001
   Amount: 1000.00

2. Complete the trip
   Edit Trip Sheet #101
   Status: FINISHED ← Important!

3. Create Billing
   Bill Number: 201
   Trip Number: 101 ← Links to trip sheet
   Amount: 1000.00
   
✅ Success!
```

## Technical Details

### Backend Validation
The system enforces this rule in [`BillingServiceImpl.java`](../backend/src/main/java/com/bobcarrental/service/impl/BillingServiceImpl.java):

```java
if (!"FINISHED".equals(tripSheet.getStatus())) {
    throw new ValidationException("Cannot create billing for non-finished trip sheet");
}
```

This is **intentional business logic** from the legacy system to ensure:
- Only completed trips are billed
- Accurate financial records
- Proper audit trail

### Status Values
- Frontend: `PENDING`, `IN_PROGRESS`, `FINISHED`, `CANCELLED`
- Backend: Same values (case-sensitive)

## Troubleshooting

If you still have issues:

1. Check browser console for errors
2. Verify backend is running
3. Check backend logs for validation messages
4. Ensure trip sheet exists and is FINISHED
5. Try manual billing (without trip number) to test

## Related Documentation
- [`TRIPSHEET_FIELD_FIXES.md`](TRIPSHEET_FIELD_FIXES.md) - Field mapping fixes
- [`BILLING_FIELD_FIXES.md`](BILLING_FIELD_FIXES.md) - Billing field fixes
- [`../backend/VALIDATION_FIXES.md`](../backend/VALIDATION_FIXES.md) - Backend validation