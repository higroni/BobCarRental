# TripSheet Frontend Field Mapping Guide

## Problems Solved

### ✅ 1. Edit Form Not Showing Dates/Times
**Solution:** Backend now properly maps entity fields to Response DTO fields with consistent naming.

### ✅ 2. Total Amount Calculation
**Solution:** Backend `getTotalAmount()` now sums ALL fare fields (hiring + extra + halt + minimum + permit + misc).

### ✅ 3. Automatic Days Calculation
**Solution:** Backend automatically calculates `days` field from `startDate` and `endDate` on create/update.

## Root Cause
Backend koristi različita imena polja u entitetu i DTO-ovima:

### Backend Entity (TripSheet.java)
```java
private LocalDate startDt;    // Database column: start_dt
private LocalDate endDt;       // Database column: end_dt
private String startTm;        // Database column: start_tm
private String endTm;          // Database column: end_tm
private String regNum;         // Database column: reg_num
```

### Backend DTOs
**TripSheetRequest.java** (što frontend šalje):
```java
private String regNum;         // Vehicle registration
private LocalDate startDate;   // Start date
private LocalDate endDate;     // End date
private String startTime;      // Start time (HH:MM)
private String endTime;        // End time (HH:MM)
```

**TripSheetResponse.java** (što backend vraća):
```java
private String regNum;         // Vehicle registration (FIXED)
private LocalDate startDate;   // Start date (mapped from startDt)
private LocalDate endDate;     // End date (mapped from endDt)
private String startTime;      // Start time (mapped from startTm)
private String endTime;        // End time (mapped from endTm)
```

## Solution Applied

### 1. Backend Mapper (TripSheetMapper.java)
Dodato mapiranje za Response:
```java
@Mapping(source = "startDt", target = "startDate")
@Mapping(source = "endDt", target = "endDate")
@Mapping(source = "startTm", target = "startTime")
@Mapping(source = "endTm", target = "endTime")
TripSheetResponse toResponse(TripSheet tripSheet);
```

### 2. Field Name Consistency
- ✅ Request i Response sada koriste **ista imena polja**
- ✅ `regNum` (ne `vehicleId`)
- ✅ `startDate`, `endDate` (ne `startDt`, `endDt`)
- ✅ `startTime`, `endTime` (ne `startTm`, `endTm`)

## Frontend Implementation Required

### TypeScript Interface (trip-sheet.model.ts)
```typescript
export interface TripSheetRequest {
  trpNum: number;
  trpDate: string;           // ISO date format
  regNum: string;            // Vehicle registration
  driver?: string;
  bookingId?: string;
  clientId: string;
  startKm: number;
  endKm: number;
  typeId: string;
  startDate: string;         // ISO date format
  endDate: string;           // ISO date format
  startTime?: string;        // HH:MM format
  endTime?: string;          // HH:MM format
  isBilled?: boolean;
  billNumber?: number;
  billDate?: string;
  status?: 'F' | 'S' | 'O';  // F=Flat, S=Split, O=Outstation
  hiring?: number;
  extra?: number;
  halt?: number;
  minimum?: number;
  time?: number;
  days?: number;
  permit?: number;
  misc?: number;
  remarks?: string;
  tagged?: boolean;
}

export interface TripSheetResponse extends TripSheetRequest {
  id: number;
  clientName?: string;
  totalKm: number;
  totalAmount: number;
  statusDescription: string;
  createdAt: string;
  updatedAt: string;
}
```

### Angular Form Mapping (trip-sheet-form.component.ts)
```typescript
// When loading data for edit:
loadTripSheet(id: number): void {
  this.tripSheetService.getTripSheet(id).subscribe(data => {
    this.tripSheetForm.patchValue({
      trpNum: data.trpNum,
      trpDate: data.trpDate,
      regNum: data.regNum,           // ✅ Correct field name
      driver: data.driver,
      clientId: data.clientId,
      startKm: data.startKm,
      endKm: data.endKm,
      typeId: data.typeId,
      startDate: data.startDate,     // ✅ Mapped from startDt
      endDate: data.endDate,         // ✅ Mapped from endDt
      startTime: data.startTime,     // ✅ Mapped from startTm
      endTime: data.endTime,         // ✅ Mapped from endTm
      isBilled: data.isBilled,
      billNumber: data.billNumber,
      billDate: data.billDate,
      status: data.status,
      hiring: data.hiring,
      extra: data.extra,
      halt: data.halt,
      minimum: data.minimum,
      time: data.time,
      days: data.days,
      permit: data.permit,
      misc: data.misc,
      remarks: data.remarks,
      tagged: data.tagged
    });
  });
}
```

### HTML Template (trip-sheet-form.component.html)
```html
<!-- Vehicle Registration -->
<mat-form-field>
  <mat-label>Vehicle Registration</mat-label>
  <input matInput formControlName="regNum" required>
</mat-form-field>

<!-- Start Date -->
<mat-form-field>
  <mat-label>Start Date</mat-label>
  <input matInput type="date" formControlName="startDate" required>
</mat-form-field>

<!-- End Date -->
<mat-form-field>
  <mat-label>End Date</mat-label>
  <input matInput type="date" formControlName="endDate" required>
</mat-form-field>

<!-- Start Time -->
<mat-form-field>
  <mat-label>Start Time</mat-label>
  <input matInput type="time" formControlName="startTime">
</mat-form-field>

<!-- End Time -->
<mat-form-field>
  <mat-label>End Time</mat-label>
  <input matInput type="time" formControlName="endTime">
</mat-form-field>
```

## Automatic Calculations

### 1. Days Calculation (Backend - TripSheetServiceImpl.java)
```java
// Automatically calculated on create/update
if (tripSheet.getStartDt() != null && tripSheet.getEndDt() != null) {
    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
        tripSheet.getStartDt(),
        tripSheet.getEndDt()
    ) + 1; // +1 to include both start and end day
    tripSheet.setDays((int) daysBetween);
}
```

**Frontend:** Days field is READ-ONLY and automatically calculated by backend.

### 2. Total Amount Calculation (Backend - TripSheet.java)
```java
public BigDecimal getTotalAmount() {
    BigDecimal total = BigDecimal.ZERO;
    
    if (hiring != null) total = total.add(hiring);
    if (extra != null) total = total.add(extra);
    if (halt != null) total = total.add(halt);
    if (minimum != null) total = total.add(minimum);
    if (permit != null) total = total.add(permit);
    if (misc != null) total = total.add(misc);
    
    return total;
}
```

**Note:** Sums ALL 6 fare fields regardless of trip status.

### Frontend Implementation
```typescript
getTotalAmount(): number {
  const hiring = this.tripSheetForm.get('hiring')?.value || 0;
  const extra = this.tripSheetForm.get('extra')?.value || 0;
  const halt = this.tripSheetForm.get('halt')?.value || 0;
  const minimum = this.tripSheetForm.get('minimum')?.value || 0;
  const permit = this.tripSheetForm.get('permit')?.value || 0;
  const misc = this.tripSheetForm.get('misc')?.value || 0;
  
  return hiring + extra + halt + minimum + permit + misc;
}
```

## Testing Checklist

- [ ] Create new TripSheet with all date/time fields
- [ ] Verify dates and times are saved correctly
- [ ] Edit existing TripSheet
- [ ] Verify all fields (including dates/times) are populated in edit form
- [ ] Verify regNum (vehicle registration) displays in list
- [ ] Verify typeId (vehicle type) displays in list
- [ ] Verify total amount calculation includes all fare fields
- [ ] Test with different status types (F, S, O)

## API Endpoints

### GET /api/tripsheets/{id}
Returns TripSheetResponse with mapped field names:
- `regNum` (not `vehicleId`)
- `startDate`, `endDate` (not `startDt`, `endDt`)
- `startTime`, `endTime` (not `startTm`, `endTm`)

### POST /api/tripsheets
Expects TripSheetRequest with:
- `regNum` (required)
- `startDate` (required)
- `endDate` (required)
- `startTime` (optional, HH:MM format)
- `endTime` (optional, HH:MM format)

### PUT /api/tripsheets/{id}
Same as POST - expects TripSheetRequest format.

## Notes

1. **Date Format**: Backend expects ISO date format (YYYY-MM-DD)
2. **Time Format**: Backend expects HH:MM format (24-hour)
3. **Time Validation**: Regex pattern `^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$`
4. **Status Values**: 
   - F = Flat Rate (local)
   - S = Split Rate (hire + fuel)
   - O = Outstation
5. **Total Amount**: Sums ALL fare fields regardless of status