# TripSheet Debugging Guide

## Problem
Datumi i vremena se ne čuvaju ili ne prikazuju u edit/view dijalogu, broj dana se ne kalkuliše automatski.

## Dijagnostika - Korak po Korak

### 1. Proveri Da Li Je MapStruct Regenerisan

**Lokacija:** `target/generated-sources/annotations/com/bobcarrental/mapper/TripSheetMapperImpl.java`

**Pokreni:**
```bash
cd bobcarrental/backend
rebuild-mappers.bat
```

**Proveri da generisana klasa sadrži:**

```java
@Override
public TripSheetResponse toResponse(TripSheet tripSheet) {
    // ...
    tripSheetResponse.setStartDate( tripSheet.getStartDt() );  // ✓ Mora biti prisutno
    tripSheetResponse.setEndDate( tripSheet.getEndDt() );      // ✓ Mora biti prisutno
    tripSheetResponse.setStartTime( tripSheet.getStartTm() );  // ✓ Mora biti prisutno
    tripSheetResponse.setEndTime( tripSheet.getEndTm() );      // ✓ Mora biti prisutno
    // ...
}

@Override
public TripSheet toEntity(TripSheetRequest request) {
    // ...
    tripSheet.setStartDt( request.getStartDate() );  // ✓ Mora biti prisutno
    tripSheet.setEndDt( request.getEndDate() );      // ✓ Mora biti prisutno
    tripSheet.setStartTm( request.getStartTime() );  // ✓ Mora biti prisutno
    tripSheet.setEndTm( request.getEndTime() );      // ✓ Mora biti prisutno
    // ...
}
```

**Ako ovo NIJE prisutno:**
- MapStruct nije regenerisao mapper
- Pokreni: `mvn clean compile` pa ponovo `mvn spring-boot:run`

---

### 2. Proveri Backend Logove Pri Kreiranju

**Pokreni backend sa debug logovima:**
```bash
mvn spring-boot:run -Dlogging.level.com.bobcarrental=DEBUG
```

**Kreiraj TripSheet i proveri log:**
```
Creating new trip sheet: 9999
Trip sheet created successfully with id: 1
```

**Dodaj debug log u TripSheetServiceImpl.java:**
```java
public TripSheetResponse createTripSheet(TripSheetRequest request) {
    log.info("Creating new trip sheet: {}", request.getTrpNum());
    log.debug("Request startDate: {}, endDate: {}", request.getStartDate(), request.getEndDate());
    log.debug("Request startTime: {}, endTime: {}", request.getStartTime(), request.getEndTime());
    
    // ... existing code ...
    
    TripSheet tripSheet = tripSheetMapper.toEntity(request);
    log.debug("Entity startDt: {}, endDt: {}", tripSheet.getStartDt(), tripSheet.getEndDt());
    log.debug("Entity startTm: {}, endTm: {}", tripSheet.getStartTm(), tripSheet.getEndTm());
    log.debug("Calculated days: {}", tripSheet.getDays());
    
    // ... rest of code ...
}
```

---

### 3. Proveri Database Schema

**Pokreni SQL query:**
```sql
SELECT * FROM trip_sheets WHERE trp_num = 9999;
```

**Proveri kolone:**
- `start_dt` - mora biti DATE tip
- `end_dt` - mora biti DATE tip
- `start_tm` - mora biti VARCHAR(5)
- `end_tm` - mora biti VARCHAR(5)
- `days` - mora biti INTEGER

**Ako kolone ne postoje ili su NULL:**
- Problem je u database schema
- Proveri `schema.sql` ili Liquibase migracije

---

### 4. Proveri Frontend Request

**Otvori Browser DevTools → Network tab**

**Kreiraj TripSheet i proveri POST request payload:**
```json
{
  "trpNum": 9999,
  "trpDate": "2024-01-15",
  "regNum": "TEST123",
  "startDate": "2024-01-15",  // ✓ Mora biti prisutno
  "endDate": "2024-01-20",    // ✓ Mora biti prisutno
  "startTime": "09:00",       // ✓ Mora biti prisutno
  "endTime": "17:00",         // ✓ Mora biti prisutno
  ...
}
```

**Ako ova polja NISU prisutna:**
- Frontend ne šalje datume
- Proveri Angular form mapping

---

### 5. Proveri Backend Response

**U Browser DevTools → Network tab**

**Proveri GET response:**
```json
{
  "id": 1,
  "trpNum": 9999,
  "startDate": "2024-01-15",  // ✓ Mora biti prisutno
  "endDate": "2024-01-20",    // ✓ Mora biti prisutno
  "startTime": "09:00",       // ✓ Mora biti prisutno
  "endTime": "17:00",         // ✓ Mora biti prisutno
  "days": 6,                  // ✓ Mora biti izračunato
  ...
}
```

**Ako ova polja su NULL ili nedostaju:**
- Problem je u backend mapperu ili database-u
- Vrati se na korak 1 i 3

---

### 6. Proveri Frontend Form Mapping

**Lokacija:** `bobcarrental/frontend/src/app/features/trip-sheet/trip-sheet-form/`

**Proveri da forma koristi pravilna imena:**
```typescript
this.tripSheetForm.patchValue({
  startDate: data.startDate,  // ✓ NE startDt
  endDate: data.endDate,      // ✓ NE endDt
  startTime: data.startTime,  // ✓ NE startTm
  endTime: data.endTime,      // ✓ NE endTm
  regNum: data.regNum,        // ✓ NE vehicleId
  ...
});
```

---

## Quick Test Script

**Koristi ovu skriptu za brzo testiranje:**

```bash
# 1. Rebuild mappers
cd bobcarrental/backend
call rebuild-mappers.bat

# 2. Restart backend
call mvn spring-boot:run

# 3. Test API (u drugom terminalu)
call test-tripsheet.bat
```

---

## Očekivani Rezultat

**Nakon uspešnog kreiranja:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "trpNum": 9999,
    "trpDate": "2024-01-15",
    "regNum": "TEST123",
    "driver": "Test Driver",
    "clientId": "CLI001",
    "startKm": 1000,
    "endKm": 1500,
    "typeId": "CAR",
    "startDate": "2024-01-15",
    "endDate": "2024-01-20",
    "startTime": "09:00",
    "endTime": "17:00",
    "days": 6,
    "status": "F",
    "hiring": 1000,
    "extra": 200,
    "halt": 150,
    "minimum": 100,
    "permit": 50,
    "misc": 25,
    "totalAmount": 1525,
    "isBilled": false,
    "tagged": false
  }
}
```

---

## Ako Problem Persisti

**Pošalji mi:**
1. Output od `rebuild-mappers.bat`
2. Backend log pri kreiranju TripSheet-a
3. SQL query rezultat: `SELECT * FROM trip_sheets WHERE trp_num = 9999;`
4. Browser Network tab screenshot (POST request payload i GET response)

**Onda ću moći da identifikujem tačan problem.**