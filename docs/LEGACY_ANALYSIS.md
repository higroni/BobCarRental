# Legacy System Analysis - Alankar Travels (1995)

## 📋 Executive Summary

Detaljana analiza legacy Clipper 5.x aplikacije za rent-a-car poslovanje. Dokument sadrži kompletnu ekstrakciju poslovne logike, validacija, izračunavanja i strukture podataka.

---

## 🗄️ Database Structure (DBF Files)

### 1. CLIENT.DBF - Klijenti
```
ClientId     C(10)   - Jedinstveni kod klijenta (PRIMARY KEY)
ClientName   C(40)   - Naziv klijenta
Address1     C(35)   - Adresa linija 1
Address2     C(30)   - Adresa linija 2
Address3     C(25)   - Adresa linija 3
Place        C(20)   - Mesto
City         C(15)   - Grad
PinCode      N(6)    - Poštanski broj
Phone        C(25)   - Telefon
Fax          C(25)   - Fax
Fare         M(10)   - Cenovnik (MEMO polje)
IsSplit      C(1)    - Dozvola za split rate (Y/N)
Tagged       L(1)    - Oznaka za filtriranje
```

**Index**: ClientId (TAG "Client")

**Validacije**:
- `PresenceChk`: ClientId mora biti jedinstven
- ClientId format: @A! (uppercase, alphanumeric)
- IsSplit: PICTURE "Y" (samo Y ili N)
- Specijalni klijent: "MISC" - za jednokratne klijente

**Biznis pravila**:
- Svaki klijent ima svoj cenovnik (Fare MEMO)
- Ako je IsSplit='Y', klijent može koristiti split rate tarifu
- MISC klijenti nemaju cenovnik, koriste standardni (FARES.TXT)
- Klijenti se NE MOGU brisati (soft delete nije implementiran)

---

### 2. VEHTYPE.DBF - Tipovi vozila
```
TypeId       C(5)    - Kod tipa vozila (PRIMARY KEY)
TypeDesc     C(50)   - Opis tipa vozila
Tagged       L(1)    - Oznaka
```

**Index**: TypeId (TAG "TypeId")

**Validacije**:
- `PresenceChk`: TypeId mora biti jedinstven
- TypeId format: @! (uppercase)

**Biznis pravila**:
- Tipovi vozila se NE MOGU brisati
- Koristi se za mapiranje u FARES.TXT (CARCODE)

---

### 3. BOOKING.DBF - Rezervacije
```
BookDate     D(8)    - Datum izvršenja (PRIMARY KEY)
TodayDate    D(8)    - Datum kreiranja rezervacije
Ref          C(15)   - Referenca
Time         C(5)    - Vreme (HH:MM format)
TypeId       C(5)    - Tip vozila (FK -> VEHTYPE)
ClientId     C(10)   - Klijent (FK -> CLIENT)
Info1        C(50)   - Informacija 1 (adresa/napomena)
Info2        C(50)   - Informacija 2
Info3        C(50)   - Informacija 3
Info4        C(50)   - Informacija 4
Tagged       L(1)    - Oznaka
```

**Index**: BookDate (TAG "BookDate")

**Validacije**:
- `CheckTime`: Validacija vremena (HH:MM, max 24:59)
- `SuperCheckIt`: TypeId mora postojati u VEHTYPE
- `SuperCheckIt`: ClientId mora postojati u CLIENT

**Biznis pravila**:
- Soft delete: DBDELETE() + SET DELETE ON
- PackUp funkcija: PACK komanda za čišćenje obrisanih
- Može se brisati (za razliku od drugih master tabela)

---

### 4. TRPSHEET.DBF - Putni listovi (Trip Sheets)
```
TrpNum       N(6)    - Broj putnog lista (PRIMARY KEY)
ClientName   C(35)   - Ime klijenta (denormalizovano)
TrpDate      D(8)    - Datum putnog lista
RegNum       C(14)   - Registarska oznaka vozila
StartKm      N(8)    - Početni kilometar
EndKm        N(8)    - Krajnji kilometar
TypeId       C(5)    - Tip vozila (FK -> VEHTYPE)
StartDt      D(8)    - Datum početka
EndDt        D(8)    - Datum završetka
StartTm      C(5)    - Vreme početka (HH:MM)
EndTm        C(5)    - Vreme završetka (HH:MM)
Driver       C(25)   - Ime vozača
ClientId     C(10)   - Klijent (FK -> CLIENT)
IsBilled     L(1)    - Da li je fakturisano
BillNum      N(6)    - Broj računa (FK -> BILLING)
BillDate     D(8)    - Datum računa
Status       C(1)    - Status: F=Flat, S=Split, O=Outstation
Hiring       N(10,2) - Iznos najma
Extra        N(10,2) - Dodatni troškovi (extra km)
Halt         N(10,2) - Troškovi noćenja
Minimum      N(10,2) - Minimalni troškovi
Time         N(4)    - Ukupno sati
Days         N(4)    - Ukupno dana
Permit       N(10,2) - Troškovi dozvole
Misc         N(10,2) - Razni troškovi
Tagged       L(1)    - Oznaka
```

**Index**: TrpNum (TAG "TripNum")

**Validacije**:
- `PresenceChk`: TrpNum mora biti jedinstven
- `CheckTime`: StartTm i EndTm validacija (HH:MM, max 24:59)
- StartKm <= EndKm
- StartDt <= EndDt
- `SuperClient`: ClientId validacija
- `SuperVehicle`: TypeId validacija
- `BillChk`: BillNum ne sme postojati u BILLING (za insert)
- `Time2Val`: Izračunavanje ukupnog vremena mora biti > 0

**Biznis pravila**:
- Putni listovi se NE MOGU brisati
- Automatski kreira BILLING i ACCOUNTS zapise
- Tri tipa putovanja:
  - **F (Flat)**: Lokalno, po satima + extra km
  - **S (Split)**: Najam + gorivo + extra km
  - **O (Outstation)**: Van grada, po danima + noćenje + dozvola

---

### 5. BILLING.DBF - Računi
```
BillNum      N(6)    - Broj računa (PRIMARY KEY)
BillDate     D(8)    - Datum računa
ClientId     C(10)   - Klijent (FK -> CLIENT)
BillImg      M(10)   - Slika računa (MEMO - generisani tekst)
TrpNum       N(6)    - Broj putnog lista (FK -> TRPSHEET)
Printed      L(1)    - Da li je štampano
Cancelled    L(1)    - Da li je otkazano
BillAmt      N(10,2) - Iznos računa
Tagged       L(1)    - Oznaka
```

**Index**: BillNum (TAG "Bill")

**Biznis pravila**:
- Računi se NE BRIŠU fizički
- Cancelled=.T. označava otkazani račun
- Otkazivanje kreira ACCOUNTS zapis sa Recd=BillAmt (povraćaj)
- BillImg se generiše automatski pri prvom prikazu
- Printed=.T. nakon prvog štampanja (ORIGINAL/COPY)

---

### 6. ACCOUNTS.DBF - Knjiga prihoda
```
Desc         C(15)   - Opis (BILL, CANCELLED BILL, PAYMENT, itd.)
Num          N(15)   - Broj dokumenta
Date         D(8)    - Datum
ClientId     C(10)   - Klijent (FK -> CLIENT)
Recd         N(12,2) - Primljeno (uplate)
Bill         N(12,2) - Fakturisano (dugovanja)
Tagged       L(1)    - Oznaka
```

**Index**: 
- ClientId (TAG "ClientId") - PRIMARY
- Num (TAG "Number") - SECONDARY

**Biznis pravila**:
- Automatski se kreira iz TRPSHEET (Desc="BILL", Bill=iznos)
- Otkazivanje računa: Desc="CANCELLED BILL", Recd=iznos
- Izveštaji: SUM(Bill) - SUM(Recd) = dugovanje
- Zapisi se NE BRIŠU

---

### 7. ADDRESS.DBF - Adresar
```
ClientId     C(10)   - Klijent (FK -> CLIENT)
Dept         C(15)   - Odeljenje
Desc         C(10)   - Opis
Name         C(40)   - Ime kontakta
Address1     C(35)   - Adresa 1
Address2     C(30)   - Adresa 2
Address3     C(25)   - Adresa 3
Place        C(20)   - Mesto
City         C(15)   - Grad
PinCode      N(6)    - Poštanski broj
Phone        C(25)   - Telefon
Fax          C(25)   - Fax
Tagged       L(1)    - Oznaka
```

**Index**: ClientId + Name (TAG "AddName")

**Validacije**:
- `ChkClient`: ClientId mora postojati u CLIENT ili biti "MISC"

**Biznis pravila**:
- Više kontakata po klijentu
- Zapisi se NE BRIŠU (samo modifikacija)

---

## 🔢 Fare Calculation Logic (TRIPMAS.PRG)

### Struktura FARES.TXT

```
[LOCAL] - Lokalne tarife (po satima)
CARCODE-HRS-RATE-FREEKMS-SPLITFARE-SPLITFUELKMSRATE

[EXTRA] - Dodatne tarife (preko osnovnih sati)
CARCODE-HRS-RATE-FREEKM-HIRERATE-HIREKM

[GENERAL] - Opšte tarife
CARECODE-FUEL_RATE_PER_KM-RATE_PER_EXCESS_KM

[OUTSTATION] - Van grada
CARECODE-RATE_PER_KM-MIN_KM_PER_DAY-NIGHT_HALT
```

### Primer (AMB vozilo):
```
[LOCAL]
AMB-1-160.00-25-110.00-50    // 1h = 160, 25km free, split=110, 50km fuel
AMB-10-475.00-100-210.00-100 // 10h = 475, 100km free

[EXTRA]
AMB-1-30.00-0-30.00-0        // +1h = 30

[GENERAL]
AMB-2.75-4.00                // Gorivo 2.75/km, extra 4.00/km

[OUTSTATION]
AMB-4.00-225-50.00           // 4/km, min 225km/dan, noćenje 50
```

---

## 💰 Fare Calculation Algorithms

### 1. FLAT RATE (Status='F') - Lokalno

```
Funkcija: LocFare(nTotTime, aFare)

Input:
  - nTotTime: Ukupno sati (izračunato iz StartTm/EndTm/Days)
  - aFare: Parsiran cenovnik

Logika:
  IF nTotTime <= MAX_LOCAL_HOURS (npr. 10h)
    - Nađi tačan sat u [LOCAL] sekciji
    - zHire = RATE za taj sat
    - zFreeKm = FREEKMS za taj sat
  ELSE
    - nTimes = INT(nTotTime / MAX_HOURS)
    - nMod = nTotTime % MAX_HOURS
    - zHire = nTimes * MAX_RATE
    - zFreeKm = nTimes * MAX_FREEKM
    IF nMod > 0
      - Dodaj iz [EXTRA] sekcije za nMod sati
    ENDIF
  ENDIF

  nKm = EndKm - StartKm
  nExtraKm = nKm - zFreeKm
  IF nExtraKm > 0
    zExtra = nExtraKm * gRateKm  // iz [GENERAL]
  ELSE
    zExtra = 0
  ENDIF

Output:
  - zHire: Osnovni najam
  - zExtra: Dodatni km
  - zMin: 0
  - zHalt: 0
  - Total = zHire + zExtra + Misc
```

**Primer**:
- 8 sati, 150 km, AMB vozilo
- zHire = 475 (za 8h)
- zFreeKm = 100
- nExtraKm = 150 - 100 = 50
- zExtra = 50 * 4.00 = 200
- **Total = 475 + 200 = 675**

---

### 2. SPLIT RATE (Status='S') - Najam + Gorivo

```
Funkcija: LocFare(nTotTime, aFare) - isti kao Flat

Dodatna logika:
  zHire = hBasic (iz SPLITFARE kolone)
  
  IF nKm <= hFuelKm (iz SPLITFUELKMSRATE)
    zMin = nKm * hKmFuel (iz [GENERAL] FUEL_RATE)
    zExtra = 0
  ELSE
    zMin = hFuelKm * hKmFuel
    zExtra = (nKm - hFuelKm) * gRateKm
  ENDIF

Output:
  - zHire: Najam (SPLITFARE)
  - zMin: Gorivo (do hFuelKm)
  - zExtra: Extra km (preko hFuelKm)
  - zHalt: 0
  - Total = zHire + zMin + zExtra + Misc
```

**Primer**:
- 8 sati, 150 km, AMB vozilo, Split rate
- zHire = 210 (split fare za 8h)
- hFuelKm = 100
- zMin = 100 * 2.75 = 275 (gorivo)
- zExtra = 50 * 4.00 = 200 (extra km)
- **Total = 210 + 275 + 200 = 685**

---

### 3. OUTSTATION (Status='O') - Van grada

```
Funkcija: OutFare(aFare)

Input:
  - aFare: Parsiran cenovnik [OUTSTATION]
  - nDays: EndDt - StartDt + 1 (kalendarski dani)
  - nKm: EndKm - StartKm

Logika:
  zHalt = (nDays - 1) * oNightHalt  // Noćenja (bez prvog dana)
  
  nMinKm = oMinKm * nDays  // Minimalni km po danu
  
  IF nKm > nMinKm
    zMin = nMinKm * oRateKm
    zExtra = (nKm - nMinKm) * oRateKm
  ELSE
    zMin = nMinKm * oRateKm  // Naplaćuje se minimum
    zExtra = 0
  ENDIF

Output:
  - zHire: 0
  - zMin: Osnovni km (minimum)
  - zExtra: Extra km
  - zHalt: Noćenja
  - Total = zMin + zExtra + zHalt + Permit + Misc
```

**Primer**:
- 3 dana, 800 km, AMB vozilo
- nMinKm = 225 * 3 = 675
- zMin = 675 * 4.00 = 2700
- zExtra = (800 - 675) * 4.00 = 500
- zHalt = 2 * 50 = 100 (2 noćenja)
- Permit = 100 (uneto ručno)
- Misc = 50 (parking)
- **Total = 2700 + 500 + 100 + 100 + 50 = 3450**

---

## ⏱️ Time Calculation (TIME2VAL Function)

```
Funkcija: Time2Val(cTime1, cTime2, nDay)

Input:
  - cTime1: Start time "HH:MM"
  - cTime2: End time "HH:MM"
  - nDay: Broj dana razlike (EndDt - StartDt)

Logika:
  nMin1 = (HH1 * 60) + MM1
  nMin2 = (HH2 * 60) + MM2
  
  IF nDay == 0 AND nMin2 > nMin1
    nTime = nMin2 - nMin1
  ENDIF
  
  IF nDay > 1
    nTime += (nDay - 1) * 24 * 60
    nDay = 1
  ENDIF
  
  IF nDay == 1
    IF nMin2 < nMin1
      nTime += ((24*60) - nMin1) + nMin2
    ELSE
      nTime += 24*60 + (nMin2 - nMin1)
    ENDIF
  ENDIF
  
  nTime = ROUND(nTime / 60, 0)  // Konverzija u sate
  
  IF nTime <= 0
    ALERT("Hours < 1, Retry")
    RETURN 0
  ENDIF

Output: Ukupno sati (zaokruženo)
```

**Primeri**:
1. Isti dan: 08:00 -> 16:30, nDay=0
   - nMin1 = 480, nMin2 = 990
   - nTime = 990 - 480 = 510 min = **8.5h → 9h**

2. Preko ponoći: 22:00 -> 02:00, nDay=1
   - nMin1 = 1320, nMin2 = 120
   - nTime = (1440 - 1320) + 120 = 240 min = **4h**

3. Više dana: 10:00 (Dan 1) -> 14:00 (Dan 3), nDay=2
   - nTime = (2-1) * 1440 + (1440 - 600) + 840
   - nTime = 1440 + 840 + 840 = 3120 min = **52h**

---

## ✅ Validation Functions

### 1. PresenceChk(oGet, xPresent)
**Svrha**: Provera jedinstvenog ključa

```
IF EMPTY(oGet:Buffer)
  RETURN .F.  // Obavezno polje
ENDIF

IF xPresent == oGet:Buffer
  RETURN .T.  // Nije promenjen, OK
ENDIF

DBSEEK(oGet:Buffer)
RETURN !FOUND()  // .T. ako ne postoji (OK), .F. ako postoji (duplikat)
```

**Koristi se za**:
- CLIENT.ClientId
- VEHTYPE.TypeId
- TRPSHEET.TrpNum
- BILLING.BillNum

---

### 2. CheckTime(oGet)
**Svrha**: Validacija vremena HH:MM

```
cTime = oGet:Buffer
nHH = VAL(SUBSTR(cTime, 1, 2))
nMM = VAL(SUBSTR(cTime, 4, 2))

IF nHH == 24 AND nMM > 0
  RETURN .F.  // 24:01 nije validno
ENDIF

IF nHH <= 24 AND nMM <= 60
  RETURN .T.
ENDIF

RETURN .F.
```

**Koristi se za**:
- BOOKING.Time
- TRPSHEET.StartTm
- TRPSHEET.EndTm

---

### 3. SuperCheckIt(oGet, AREA, HOMEAREA)
**Svrha**: Foreign key validacija

```
IF AREA == "CLIENT" AND SUBSTR(oGet:Buffer, 1, 4) == "MISC"
  RETURN .T.  // MISC je specijalan slučaj
ENDIF

DBSELECTAR(AREA)

IF EMPTY(oGet:Buffer)
  RETURN .F.
ENDIF

nFound = DBSEEK(oGet:Buffer)

IF !nFound
  ALERT("Invalid data")
ENDIF

DBSELECTAR(HOMEAREA)
RETURN nFound
```

**Koristi se za**:
- BOOKING.TypeId -> VEHTYPE
- BOOKING.ClientId -> CLIENT
- TRPSHEET.TypeId -> VEHTYPE
- TRPSHEET.ClientId -> CLIENT

---

### 4. ChkClient(Type)
**Svrha**: Validacija ClientId (sa MISC podrškom)

```
IF RTRIM(Type) == "MISC"
  RETURN .T.
ENDIF

DBSELECTAR("CLIENT")
lFound = DBSEEK(Type)
DBSELECTAR(original_area)
RETURN lFound
```

**Koristi se za**:
- ACCOUNTS.ClientId
- ADDRESS.ClientId

---

### 5. BillChk(cBill)
**Svrha**: Provera da BillNum ne postoji (za insert)

```
DBSELECTAR("BILLING")
Res = DBSEEK(VAL(cBill))
DBSELECTAR("TRPSHEET")
RETURN !Res  // .T. ako ne postoji (OK za insert)
```

---

## 📊 Report Generation

### Bill Format (BILLER.PRG)

```
Header (HEADER.TXT - 6 linija)
_____________________________________________________________________

Bill. No.: MMM/NNNNNN/YY                    | DATE : DD/MM/YYYY
_____________________________________________________________________

To.                                         |
  ClientName                                | ATTN:
  Address1                                  | -----
  Address2                                  |
  Address3 Place                            |
  City    Pin: NNNNNN                       |
=====================================================================

Charges for the car (VehicleTypeDesc)
engaged by you as per our Con. No: NNNNNN   Dated: DD/MM/YYYY

TOTAL USED KMS................  NNNNN

[FLAT RATE]
TOTAL USED HOURS..............  NNNNN

Hiring Charges.....................................Rs. 99999999.99
Ext. Km Charges....................................Rs. 99999999.99

[SPLIT RATE]
TOTAL USED HOURS..............  NNNNN

Hiring Charges.....................................Rs. 99999999.99
Fuel Cost..........................................Rs. 99999999.99
Extra Km Charges...................................Rs. 99999999.99

[OUTSTATION]
TOTAL CALENDAR DAYS...........  NNNNN

Total Charges......................................Rs. 99999999.99
Night Halt Charges.................................Rs. 99999999.99
Permit Charges.....................................Rs. 99999999.99

Misc. Expenses (Parking charges, etc.).............Rs. 99999999.99

                                                ===============
TOTAL AMOUNT.......................................Rs. 99999999.99
                                                ===============

(Rupees XXXXXXX & YY paise only)

E. & O.E.

                                        for COMPANY_NAME


_____________________________________________________________________
DD/MM/YYYY                                              HH:MM:SS
```

---

## 🔐 Security & Access Control

**Legacy sistem NEMA security**:
- Nema login/logout
- Nema role-based access
- Nema audit trail
- Svi korisnici imaju pun pristup

**Za modernu aplikaciju**:
- ADMIN role: Pristup StandardFare i HeaderTemplate
- USER role: Sve ostalo
- Audit fields: createdAt, updatedAt, createdBy, updatedBy

---

## 🎯 Key Business Rules Summary

### 1. Master Data
- CLIENT, VEHTYPE, ADDRESS: **NE MOGU se brisati**
- BOOKING: **Može se brisati** (soft delete + PACK)
- TRPSHEET, BILLING, ACCOUNTS: **NE MOGU se brisati**

### 2. Relationships
- TRPSHEET -> CLIENT (ClientId)
- TRPSHEET -> VEHTYPE (TypeId)
- TRPSHEET -> BILLING (BillNum) - 1:1
- BILLING -> ACCOUNTS (BillNum) - 1:1
- BOOKING -> CLIENT (ClientId)
- BOOKING -> VEHTYPE (TypeId)
- ADDRESS -> CLIENT (ClientId) - Many:1

### 3. Automatic Processes
- TRPSHEET insert → automatski kreira BILLING i ACCOUNTS
- BILLING cancel → kreira ACCOUNTS zapis sa Recd=BillAmt
- BILLING prvi prikaz → generiše BillImg (MEMO)

### 4. Special Cases
- ClientId="MISC": Jednokratni klijenti, koriste FARES.TXT
- IsSplit='Y': Klijent može koristiti Split rate
- Status='F'/'S'/'O': Različite formule za izračunavanje

### 5. Calculations
- Flat: Hire + Extra
- Split: Hire + Fuel + Extra
- Outstation: MinKm + Extra + Halt + Permit + Misc
- Time: Zaokruženo na ceo sat (ROUND)

---

## 📝 Migration Notes

### Data to Migrate
1. ✅ CLIENT.DBF → clients table
2. ✅ VEHTYPE.DBF → vehicle_types table
3. ✅ BOOKING.DBF → bookings table
4. ✅ TRPSHEET.DBF → trip_sheets table
5. ✅ BILLING.DBF → billings table
6. ✅ ACCOUNTS.DBF → accounts table
7. ✅ ADDRESS.DBF → addresses table
8. ✅ FARES.TXT → standard_fares table (parsirati)
9. ✅ HEADER.TXT → header_templates table

### Data Transformations
- MEMO polja (Fare, BillImg) → TEXT ili CLOB
- Logical (.T./.F.) → BOOLEAN
- Date (YYYYMMDD) → DATE
- Numeric → DECIMAL(10,2)
- Character → VARCHAR

### Validation Migration
- PresenceChk → @Column(unique=true) + service validation
- CheckTime → @Pattern regex + custom validator
- SuperCheckIt → @ManyToOne + @JoinColumn
- ChkClient → Custom validator sa MISC support

### Calculation Migration
- LocFare → FareCalculationService.calculateFlat()
- OutFare → FareCalculationService.calculateOutstation()
- Time2Val → DateTimeUtil.calculateHours()
- fig2word → NumberToWordsUtil.convert()

---

## 🚀 Next Steps

1. ✅ Kreirati JPA entitete prema DBF strukturi
2. ✅ Implementirati validation service
3. ✅ Implementirati fare calculation service
4. ✅ Kreirati migration service za DBF → H2
5. ✅ Parsirati FARES.TXT i HEADER.TXT
6. ✅ Implementirati report generation (PDF)
7. ✅ Dodati security layer (JWT + RBAC)
8. ✅ Kreirati REST API endpoints
9. ✅ Implementirati Angular frontend
10. ✅ Kreirati automatizovane testove

---

**Dokument kreiran**: 2026-03-26  
**Autor**: Bob (AI Software Engineer)  
**Verzija**: 1.0  
**Status**: ✅ Kompletna analiza