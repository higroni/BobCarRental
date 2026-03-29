# Bob Car Rental - Quick Start Guide

## 🚀 Kako Pokrenuti Aplikaciju

### Preduslov
- **Java 21** instaliran
- **Maven** instaliran
- **Node.js 18+** (LTS verzija, izbegavati 25.x)
- **npm 9+**

---

## 📦 Korak 1: Pokretanje Backend-a

### Opcija A: Automatsko Pokretanje (Preporučeno)

```bash
cd bobcarrental/backend
.\clean-restart.bat
```

Ova komanda će:
- Očistiti prethodne build fajlove
- Kompajlirati projekat
- Pokrenuti Spring Boot aplikaciju
- Backend će biti dostupan na `http://localhost:8080`

### Opcija B: Manuelno Pokretanje

```bash
cd bobcarrental/backend
mvnw clean install
mvnw spring-boot:run
```

### Provera Backend-a

Backend je uspešno pokrenut kada vidite:
```
Started BobCarRentalApplication in X.XXX seconds
```

**API Dokumentacija:** `http://localhost:8080/api/v1`

---

## 🎨 Korak 2: Pokretanje Frontend-a

### Instalacija Zavisnosti (Samo Prvi Put)

```bash
cd bobcarrental/frontend
npm install
```

### Pokretanje Development Servera

```bash
ng serve
```

ili

```bash
npm start
```

Frontend će biti dostupan na: **`http://localhost:4200`**

---

## 🔐 Korak 3: Prijavljivanje

### Default Kredencijali

Aplikacija dolazi sa unapred kreiranim korisnicima:

#### Admin Nalog
- **Username:** `admin`
- **Password:** `admin123`
- **Pristup:** Sve funkcionalnosti + admin moduli

#### Obični Korisnik
- **Username:** `user`
- **Password:** `user123`
- **Pristup:** Osnovne funkcionalnosti (bez admin modula)

---

## 📋 Korak 4: Testiranje Funkcionalnosti

### 1. Login Stranica
- Otvorite `http://localhost:4200`
- Automatski ćete biti preusmereni na `/login`
- Unesite kredencijale (admin/admin123)
- Kliknite "Sign In"

### 2. Dashboard
- Nakon uspešnog logovanja, videćete Dashboard
- Prikazuje se:
  - Toolbar sa imenom korisnika i rolom
  - Kartice za sve dostupne module
  - Quick Stats sekcija (placeholder)
  - Logout dugme

### 3. Navigacija
- Kliknite na bilo koju karticu modula
- Trenutno će pokazati grešku jer moduli još nisu implementirani
- To je očekivano ponašanje

### 4. Logout
- Kliknite na logout ikonu u toolbar-u
- Bićete vraćeni na login stranicu

---

## 🧪 Testiranje Backend API-ja

### Automatski Test (Preporučeno)

```bash
cd bobcarrental/backend
.\test-api-full.bat
```

Ovaj test proverava:
- ✅ Login sa ispravnim kredencijalima
- ✅ Login sa pogrešnim kredencijalima
- ✅ Pristup zaštićenim endpoint-ima
- ✅ Token refresh funkcionalnost
- ✅ CRUD operacije za sve module
- ✅ Logout funkcionalnost

**Očekivani rezultat:** Svi testovi prolaze (10/10)

### Manuelni Test sa cURL

```bash
# 1. Login
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"

# 2. Dobijte token iz odgovora i koristite ga
curl -X GET http://localhost:8080/api/v1/clients ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 📊 Status Projekta

### ✅ Kompletno (Backend - 100%)
- 10 CRUD modula
- JWT autentifikacija
- Role-based access control
- Database migracije
- API testovi

### ✅ Kompletno (Frontend - 50%)
- Angular 21 projekat
- TypeScript modeli (10)
- AuthService
- HTTP Interceptori
- Auth Guards
- Login komponenta
- Dashboard komponenta
- Routing konfiguracija

### ⏳ U Toku (Frontend - 50%)
- CRUD moduli (0/10)
- Shared komponente
- E2E testovi
- Docker konfiguracija

---

## 🗂️ Struktura Projekta

```
bobcarrental/
├── backend/                    # Spring Boot API
│   ├── src/main/java/         # Java source kod
│   ├── src/main/resources/    # Konfiguracija i migracije
│   └── pom.xml                # Maven zavisnosti
│
├── frontend/                   # Angular aplikacija
│   ├── src/app/
│   │   ├── core/              # Servisi, guards, interceptori
│   │   ├── features/          # Login, Dashboard
│   │   ├── models/            # TypeScript interfejsi
│   │   └── shared/            # Deljene komponente
│   └── package.json           # npm zavisnosti
│
├── migration/                  # Python skripte za migraciju
└── docs/                      # Dokumentacija
```

---

## 🔧 Česti Problemi i Rešenja

### Problem: Backend se ne pokreće
**Rešenje:**
```bash
# Proverite Java verziju
java -version  # Mora biti 21+

# Očistite Maven cache
mvnw clean

# Pokrenite ponovo
.\clean-restart.bat
```

### Problem: Frontend pokazuje grešku pri instalaciji
**Rešenje:**
```bash
# Obrišite node_modules i package-lock.json
rm -rf node_modules package-lock.json

# Instalirajte ponovo
npm install
```

### Problem: Port 8080 je zauzet
**Rešenje:**
```bash
# Promenite port u application.properties
server.port=8081
```

### Problem: Port 4200 je zauzet
**Rešenje:**
```bash
# Pokrenite na drugom portu
ng serve --port 4201
```

### Problem: CORS greška
**Rešenje:**
Backend već ima CORS konfiguraciju za `http://localhost:4200`. Ako koristite drugi port, ažurirajte `SecurityConfig.java`.

---

## 📝 Sledeći Koraci

1. **Testirajte Login i Dashboard** ✅
2. **Implementirajte prvi CRUD modul** (Client Management)
3. **Kreirajte shared komponente** (Loading, Notifications)
4. **Implementirajte preostalih 9 CRUD modula**
5. **Dodajte E2E testove**
6. **Kreirajte Docker konfiguraciju**

---

## 📚 Dodatna Dokumentacija

- **Backend README:** `bobcarrental/backend/BACKEND_README.md`
- **Frontend README:** `bobcarrental/frontend/FRONTEND_README.md`
- **Progress Report:** `bobcarrental/FRONTEND_PROGRESS.md`
- **API Testing:** `bobcarrental/backend/API_TESTING.md`
- **Migration Guide:** `bobcarrental/migration/README.md`

---

## 🆘 Podrška

Ako naiđete na probleme:
1. Proverite log fajlove:
   - Backend: `bobcarrental/backend/app.log`
   - Frontend: Browser Developer Console (F12)
2. Proverite da li su oba servera pokrenuta
3. Proverite da li su portovi dostupni

---

## 🎉 Uspešno Pokretanje

Ako vidite:
- ✅ Backend radi na `http://localhost:8080`
- ✅ Frontend radi na `http://localhost:4200`
- ✅ Možete se prijaviti sa admin/admin123
- ✅ Dashboard se prikazuje sa svim modulima

**Čestitamo! Aplikacija je uspešno pokrenuta!** 🚀

---

**Poslednje ažurirano:** 2026-03-28  
**Verzija:** 1.0.0