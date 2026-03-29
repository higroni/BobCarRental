# Booking Module - Legacy Field Mapping

## Problem
Backend Booking entity koristi **legacy strukturu** iz starog DBF sistema (BOOKING.DBF iz 1995), dok je frontend inicijalno implementiran sa modernom strukturom.

## Backend Legacy Structure (BookingRequest.java)

```java
@NotNull LocalDate bookDate;        // Reporting/execution date
@NotNull LocalDate todayDate;       // Order/creation date  
@NotBlank String time;              // Time in HH:MM format (required)
String ref;                         // Reference number (optional)
@NotBlank String typeId;            // Vehicle type ID (String, max 5 chars)
@NotBlank String clientId;          // Client ID (String, max 10 chars)
String info1, info2, info3, info4;  // 4 info lines (max 50 chars each)
Boolean tagged;                     // Tagged for filtering
```

## Frontend Mapping

### Form Fields (booking-form.ts)
```typescript
bookingForm = {
  ref: string (optional)
  bookDate: Date (required)
  todayDate: Date (required)
  time: string (required, pattern: HH:MM)
  clientId: string (required) - uses client.clientId from dropdown
  vehicleTypeId: string (required) - uses type.typeId from dropdown
  info1: string (optional, max 50)
  info2: string (optional, max 50)
  info3: string (optional, max 50)
  info4: string (optional, max 50)
  tagged: boolean (default: false)
}
```

### Dropdown Mapping (IMPORTANT!)
```html
<!-- Client dropdown uses clientId (String), not id (Long) -->
<mat-option *ngFor="let client of clients" [value]="client.clientId">
  {{ client.clientName }}
</mat-option>

<!-- VehicleType dropdown uses typeId (String), not id (Long) -->
<mat-option *ngFor="let type of vehicleTypes" [value]="type.typeId">
  {{ type.typeName }}
</mat-option>
```

### Request Transformation (onSubmit)
```typescript
const request: BookingRequest = {
  ref: formValue.ref || undefined,
  bookDate: formatDate(formValue.bookDate),
  todayDate: formatDate(formValue.todayDate),
  time: formValue.time,
  typeId: formValue.vehicleTypeId,    // Already string from dropdown
  clientId: formValue.clientId,       // Already string from dropdown
  info1: formValue.info1 || undefined,
  info2: formValue.info2 || undefined,
  info3: formValue.info3 || undefined,
  info4: formValue.info4 || undefined,
  tagged: formValue.tagged || false
};
```

## Field Usage

### Info Lines (info1-4)
Legacy system koristio ove linije za:
- **info1**: Pickup location
- **info2**: Drop location  
- **info3**: Special instructions
- **info4**: Additional notes

### Time Field
- Format: `HH:MM` (00:00-24:59)
- Validacija: Pattern regex `^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$`
- Obavezno polje

### Date Fields
- **bookDate**: Datum izvršenja/reportinga rezervacije
- **todayDate**: Datum kreiranja rezervacije (obično današnji datum)

### ID Fields (CRITICAL!)
- Backend očekuje **String** tipove za `typeId` i `clientId`
- Backend traži entitete po **business key** poljima:
  - `Client.clientId` (String, npr. "CLI001") - NE po `Client.id` (Long)
  - `VehicleType.typeId` (String, npr. "VT001") - NE po `VehicleType.id` (Long)
- Frontend dropdowns MORAJU koristiti `clientId` i `typeId`, ne `id`
- Bez konverzije - dropdown direktno vraća String vrednosti

## Future Modernization

Kada budemo refaktorisali backend, trebalo bi:

1. Dodati moderne polje:
   - `startDate`, `endDate` umesto samo `bookDate`
   - `startTime`, `endTime` umesto samo `time`
   - `pickupLocation`, `dropLocation` umesto `info1`, `info2`
   - `estimatedKm`, `estimatedAmount`, `advanceAmount`
   - `remarks` umesto `info3`, `info4`
   - `status` enum (PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)

2. Promeniti ID tipove:
   - `vehicleTypeId: Long` umesto `typeId: String`
   - `clientId: Long` umesto `clientId: String`

3. Dodati relacione veze:
   - `@ManyToOne VehicleType vehicleType`
   - `@ManyToOne Client client`

## Files Modified

1. `models/booking.model.ts` - Updated interfaces to match backend
2. `features/bookings/booking-form/booking-form.ts` - Updated form fields and submission logic
3. `features/bookings/booking-form/booking-form.html` - Updated template with legacy fields

## Testing

Test kreiranje bookinga sa:
```json
{
  "bookDate": "2026-03-29",
  "todayDate": "2026-03-29", 
  "time": "14:30",
  "typeId": "1",
  "clientId": "1",
  "info1": "Airport pickup",
  "info2": "Hotel drop",
  "tagged": false
}
```

---
Made with Bob