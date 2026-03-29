# Kako Pristupiti H2 Database Console

## Pristup H2 Konzoli

### 1. Pokreni Backend
```bash
cd bobcarrental/backend
mvn spring-boot:run
```

### 2. Otvori H2 Console u Browseru
```
http://localhost:8080/h2-console
```

### 3. Login Podaci

**JDBC URL:** `jdbc:h2:file:./data/bobcarrental`
**User Name:** `sa`
**Password:** *(ostavi prazno)*

Klikni **Connect**

---

## SQL Queries Za Dijagnostiku

### 1. Proveri TripSheet Schema
```sql
SHOW COLUMNS FROM TRIP_SHEETS;
```

**Očekivane kolone:**
- `START_DT` - DATE
- `END_DT` - DATE
- `START_TM` - VARCHAR(5)
- `END_TM` - VARCHAR(5)
- `DAYS` - INTEGER

---

### 2. Proveri Postojeće TripSheet Podatke
```sql
SELECT 
    ID,
    TRP_NUM,
    TRP_DATE,
    REG_NUM,
    START_DT,
    END_DT,
    START_TM,
    END_TM,
    DAYS,
    HIRING,
    EXTRA,
    HALT,
    MINIMUM,
    PERMIT,
    MISC
FROM TRIP_SHEETS
ORDER BY ID DESC
LIMIT 10;
```

---

### 3. Proveri Specifičan TripSheet
```sql
SELECT * 
FROM TRIP_SHEETS 
WHERE TRP_NUM = 9999;
```

---

### 4. Proveri Da Li Su Datumi NULL
```sql
SELECT 
    TRP_NUM,
    START_DT IS NULL AS start_is_null,
    END_DT IS NULL AS end_is_null,
    START_TM IS NULL AS start_time_is_null,
    END_TM IS NULL AS end_time_is_null,
    DAYS
FROM TRIP_SHEETS
WHERE TRP_NUM = 9999;
```

**Ako su svi NULL:**
- Problem je što backend ne čuva datume u bazu
- Proveri MapStruct mapper implementaciju

**Ako NISU NULL:**
- Problem je u backend Response mapperu
- Datumi su u bazi ali se ne vraćaju u API response-u

---

### 5. Ručno Unesi Test Podatke
```sql
INSERT INTO TRIP_SHEETS (
    TRP_NUM, TRP_DATE, REG_NUM, DRIVER, CLIENT_ID, 
    START_KM, END_KM, TYPE_ID,
    START_DT, END_DT, START_TM, END_TM, DAYS,
    STATUS, HIRING, EXTRA, HALT, MINIMUM, PERMIT, MISC,
    IS_BILLED, TAGGED, DELETED,
    CREATED_AT, UPDATED_AT
) VALUES (
    8888, '2024-01-15', 'TEST999', 'Test Driver', 'CLI001',
    1000, 1500, 'CAR',
    '2024-01-15', '2024-01-20', '09:00', '17:00', 6,
    'F', 1000, 200, 150, 100, 50, 25,
    FALSE, FALSE, FALSE,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
```

Zatim testiraj GET request:
```bash
curl -X GET http://localhost:8080/api/tripsheets/8888 -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. Proveri Sve Tabele
```sql
SHOW TABLES;
```

---

### 7. Obriši Test Podatke
```sql
DELETE FROM TRIP_SHEETS WHERE TRP_NUM IN (8888, 9999);
```

---

## Dijagnostika Problema

### Scenario 1: Kolone Ne Postoje
```sql
SHOW COLUMNS FROM TRIP_SHEETS;
```

**Ako `START_DT`, `END_DT` ne postoje:**
- Problem je u database schema
- Proveri Liquibase migracije ili `schema.sql`

---

### Scenario 2: Kolone Postoje Ali Su NULL
```sql
SELECT START_DT, END_DT FROM TRIP_SHEETS WHERE TRP_NUM = 9999;
```

**Ako su NULL:**
- Backend ne mapira Request → Entity pravilno
- Proveri `TripSheetMapperImpl.java` u `target/generated-sources/`

---

### Scenario 3: Podaci Su U Bazi Ali Se Ne Vraćaju
```sql
-- Podaci su u bazi
SELECT START_DT, END_DT FROM TRIP_SHEETS WHERE TRP_NUM = 9999;
-- Rezultat: START_DT = 2024-01-15, END_DT = 2024-01-20
```

**Ali API vraća NULL:**
- Backend ne mapira Entity → Response pravilno
- Proveri `TripSheetMapperImpl.java` metodu `toResponse()`

---

## Lokacija Database Fajla

**Windows:**
```
d:\POSAO\alankar\bobcarrental\backend\data\bobcarrental.mv.db
```

**Backup:**
```bash
copy data\bobcarrental.mv.db data\bobcarrental.mv.db.backup
```

---

## Restart Database (Ako Treba)

1. Zaustavi backend (Ctrl+C)
2. Obriši database fajl:
```bash
del data\bobcarrental.mv.db
```
3. Pokreni backend ponovo - kreiraće novu bazu sa Liquibase migracijama

---

## Korisni Linkovi

- **H2 Console:** http://localhost:8080/h2-console
- **API Docs:** http://localhost:8080/swagger-ui.html (ako je Swagger konfigurisan)
- **Actuator Health:** http://localhost:8080/actuator/health