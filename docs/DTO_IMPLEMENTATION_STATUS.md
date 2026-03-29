# DTO Implementation Status

## ✅ Završeno (Completed)

### Common DTOs
- ✅ `ApiResponse<T>` - Standardni wrapper za sve API odgovore
- ✅ `ErrorDetails` - Detalji o greškama
- ✅ `FieldError` - Validacione greške za polja
- ✅ `PageResponse<T>` - Wrapper za paginirane odgovore

### Authentication DTOs
- ✅ `LoginRequest` - Request za login
- ✅ `AuthResponse` - Response sa JWT tokenima i user info

### Client DTOs
- ✅ `ClientRequest` - Request za kreiranje/ažuriranje klijenta
- ✅ `ClientResponse` - Response sa punim detaljima klijenta
- ✅ `ClientSummaryResponse` - Response sa sažetkom klijenta (za liste)
- ✅ `ClientMapper` - MapStruct mapper za Client

## 📋 Preostalo za Implementaciju

### Booking DTOs
```java
// Request
- BookingRequest (bookDate, time, ref, typeId, clientId, info1-4, tagged)

// Response
- BookingResponse (puni detalji)
- BookingSummaryResponse (za liste)

// Mapper
- BookingMapper
```

### VehicleType DTOs
```java
// Request
- VehicleTypeRequest (typeId, typeDesc, tagged)

// Response
- VehicleTypeResponse (puni detalji + images)
- VehicleTypeSummaryResponse (za liste)

// Mapper
- VehicleTypeMapper
```

### VehicleImage DTOs
```java
// Request
- VehicleImageUploadRequest (multipart file)

// Response
- VehicleImageResponse (sa base64 ili URL)
- VehicleImageSummaryResponse (bez image data)

// Mapper
- VehicleImageMapper
```

### TripSheet DTOs
```java
// Request
- TripSheetRequest (tripNumber, tripDate, vehicleId, driverId, clientId, 
                    startKm, endKm, typeId, startDate, endDate, startTime, 
                    endTime, status, hiring, extra, halt, minimum, permit, misc)

// Response
- TripSheetResponse (puni detalji + calculated fields)
- TripSheetSummaryResponse (za liste)

// Mapper
- TripSheetMapper
```

### Billing DTOs
```java
// Request
- BillingRequest (billNumber, billDate, clientId, tripNumber, billAmount)

// Response
- BillingResponse (puni detalji)
- BillingSummaryResponse (za liste)

// Mapper
- BillingMapper
```

### Account DTOs
```java
// Request
- AccountRequest (accountDate, clientId, description, documentNumber, 
                  received, billed)

// Response
- AccountResponse (puni detalji + balance)
- AccountSummaryResponse (za liste)

// Mapper
- AccountMapper
```

### Address DTOs
```java
// Request
- AddressRequest (clientId, department, description, name, address1-3, 
                  place, city, pinCode, phone, fax)

// Response
- AddressResponse (puni detalji)
- AddressSummaryResponse (za liste)

// Mapper
- AddressMapper
```

### StandardFare DTOs (ADMIN ONLY)
```java
// Request
- StandardFareRequest (vehicleCode, fareType, baseRate, perKmRate, 
                       perHourRate, minimumCharge, description, 
                       effectiveFrom, effectiveTo)

// Response
- StandardFareResponse (puni detalji)
- StandardFareSummaryResponse (za liste)

// Mapper
- StandardFareMapper
```

### HeaderTemplate DTOs (ADMIN ONLY)
```java
// Request
- HeaderTemplateRequest (templateName, templateType, headerText, 
                         footerText, variables, isActive)

// Response
- HeaderTemplateResponse (puni detalji)
- HeaderTemplateSummaryResponse (za liste)

// Mapper
- HeaderTemplateMapper
```

## 📝 Napomene za Implementaciju

### Validacije
Svi Request DTO-ovi treba da imaju:
- `@NotBlank` za obavezna polja
- `@Size` za ograničenja dužine
- `@Pattern` za format validacije (npr. vreme HH:MM)
- `@Min/@Max` za numeričke vrednosti
- Custom validacije za legacy pravila (PresenceChk, CheckTime, SuperCheckIt)

### Response DTO-ovi
- **Full Response**: Svi detalji entiteta + calculated fields
- **Summary Response**: Samo najvažnija polja za prikaz u tabelama
- Uvek uključiti `id`, `createdAt`, `updatedAt`

### MapStruct Mapperi
Svaki mapper treba da ima:
```java
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
```

Metode:
- `toEntity(Request)` - za kreiranje
- `toResponse(Entity)` - puni detalji
- `toSummaryResponse(Entity)` - sažetak
- `toResponseList(List<Entity>)` - lista punih detalja
- `toSummaryResponseList(List<Entity>)` - lista sažetaka
- `updateEntityFromRequest(Request, @MappingTarget Entity)` - za update

### Calculated Fields
Koristiti `@Mapping(target = "field", expression = "java(entity.getCalculatedField())")` za:
- `fullAddress` u Client
- `totalKm` u TripSheet
- `totalAmount` u TripSheet
- `balance` u Account
- itd.

## 🎯 Prioritet Implementacije

1. **Visok prioritet** (Core funkcionalnost):
   - Booking DTOs
   - TripSheet DTOs
   - Billing DTOs

2. **Srednji prioritet**:
   - VehicleType DTOs
   - Account DTOs
   - Address DTOs

3. **Nizak prioritet** (ADMIN only):
   - StandardFare DTOs
   - HeaderTemplate DTOs

## 📊 Statistika

- ✅ **Završeno**: 10 DTO-ova (Common + Auth + Client)
- 📋 **Preostalo**: ~50 DTO-ova (Request + Response + Mappers)
- 🎯 **Ukupno**: ~60 DTO-ova

---

**Napomena**: Svi DTO-ovi prate isti pattern kao Client DTOs.
Kopirajte i prilagodite ClientRequest/ClientResponse/ClientMapper za ostale entitete.

**Datum**: 2026-03-26  
**Status**: U toku (10/60 završeno)