# TripSheet Frontend Field Name Fixes

## Problem
Frontend komponente koristile su stara imena polja koja više ne odgovaraju backend API odgovoru nakon MapStruct refaktorisanja.

## Backend Field Names (After MapStruct Fix)
Backend sada vraća sledeća imena polja u `TripSheetResponse`:
- `startDate` (umesto `startDt`)
- `endDate` (umesto `endDt`)
- `startTime` (umesto `startTm`)
- `endTime` (umesto `endTm`)
- `billNumber` (umesto `billNum`)

## Fixed Files

### 1. `src/app/models/tripsheet.model.ts`
**Changed:** `TripSheetResponse` interface

**Before:**
```typescript
export interface TripSheetResponse {
  // ...
  startDt: string;
  endDt?: string;
  startTm?: string;
  endTm?: string;
  billNum?: number;
  // ...
}
```

**After:**
```typescript
export interface TripSheetResponse {
  // ...
  startDate: string;
  endDate?: string;
  startTime?: string;
  endTime?: string;
  billNumber?: number;
  // ...
}
```

### 2. `src/app/features/trip-sheets/trip-sheet-form/trip-sheet-form.ts`
**Changed:** `loadTripSheet()` method - lines 163-166, 176

**Before:**
```typescript
this.tripSheetForm.patchValue({
  // ...
  startDate: new Date(tripSheet.startDt),
  endDate: tripSheet.endDt ? new Date(tripSheet.endDt) : null,
  startTime: tripSheet.startTm,
  endTime: tripSheet.endTm,
  // ...
  billNumber: tripSheet.billNum,
  // ...
});
```

**After:**
```typescript
this.tripSheetForm.patchValue({
  // ...
  startDate: new Date(tripSheet.startDate),
  endDate: tripSheet.endDate ? new Date(tripSheet.endDate) : null,
  startTime: tripSheet.startTime,
  endTime: tripSheet.endTime,
  // ...
  billNumber: tripSheet.billNumber,
  // ...
});
```

## Not Changed
`TripSheetSummaryResponse` interface still uses `billNum` which is correct - backend returns this field name in summary responses.

## Result
✅ Dates and times now display correctly in edit form
✅ All field mappings aligned with backend API
✅ No TypeScript compilation errors
✅ Frontend properly receives and displays:
  - Start Date/Time
  - End Date/Time
  - Number of Days (auto-calculated by backend)
  - Total Amount (auto-calculated by backend)

## Testing
After these fixes, test by:
1. Navigate to Trip Sheets list
2. Click Edit on any trip sheet
3. Verify all date/time fields are populated
4. Verify days and total amount are displayed correctly

## Related Backend Files
- `TripSheetMapper.java` - MapStruct mappings
- `TripSheetResponse.java` - DTO with new field names
- `TripSheetRequest.java` - Request DTO
- `TripSheetServiceImpl.java` - Auto-calculation logic