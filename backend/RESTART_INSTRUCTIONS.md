# 🔄 Restart Aplikacije - Instrukcije

## Brzi Start

Jednostavno pokrenite:
```cmd
restart-app.bat
```

Skripta će automatski:
1. ✅ Zaustaviti sve Java procese (trenutnu aplikaciju)
2. ✅ Očistiti stare build artifakte (`mvn clean`)
3. ✅ Kompajlirati aplikaciju (`mvn compile`)
4. ✅ Pokrenuti Spring Boot aplikaciju (`mvn spring-boot:run`)

## Šta Radi Skripta

### Korak 1: Zaustavljanje
```cmd
taskkill /F /PID <java_process_id>
```
- Pronalazi sve Java procese
- Zaustavlja ih forsiranim kill komandom
- Čeka 2 sekunde da se procesi završe

### Korak 2: Čišćenje
```cmd
mvn clean
```
- Briše `target/` direktorijum
- Uklanja sve kompajlirane klase
- Priprema za čist build

### Korak 3: Kompajliranje
```cmd
mvn compile
```
- Kompajlira sve Java klase
- Generiše MapStruct mappere
- Proverava sintaksu

### Korak 4: Pokretanje
```cmd
mvn spring-boot:run
```
- Pokreće Spring Boot aplikaciju
- Liquibase kreira tabele i učitava seed podatke
- Aplikacija dostupna na http://localhost:8080

## Očekivani Rezultat

Nakon uspešnog pokretanja videćete:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.3)

...
Liquibase: Successfully acquired change log lock
Liquibase: Update command completed successfully
...
Started BobCarRentalApplication in X.XXX seconds
```

## Verifikacija

### 1. Provera Liquibase Logova
Tražite u logovima:
```
Liquibase: Successfully acquired change log lock
Liquibase: Running Changeset: db/changelog/001-create-roles-table.yaml
Liquibase: Running Changeset: db/changelog/002-create-users-table.yaml
Liquibase: Running Changeset: db/changelog/003-create-user-roles-table.yaml
Liquibase: Running Changeset: db/changelog/014-seed-roles-and-admin.yaml
Liquibase: Update command completed successfully
```

### 2. Provera Seed Podataka
Otvorite H2 Console: http://localhost:8080/h2-console

**Konekcija:**
- JDBC URL: `jdbc:h2:file:./data/bobcarrental`
- Username: `sa`
- Password: (prazno)

**SQL Query:**
```sql
SELECT * FROM users;
SELECT * FROM roles;
SELECT * FROM user_roles;
```

Trebalo bi da vidite:
- 1 korisnik: `admin`
- 2 role: `ROLE_ADMIN`, `ROLE_USER`
- 1 user_role mapiranje

### 3. Test Login
Pokrenite test skriptu:
```powershell
.\test-api.ps1
```

Ili manuelno:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"admin","password":"admin"}' |
  Select-Object -ExpandProperty Content
```

Očekivani odgovor:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

## Troubleshooting

### Problem: "Port 8080 already in use"
**Rešenje:**
```cmd
netstat -ano | findstr :8080
taskkill /F /PID <PID>
```

### Problem: "Liquibase lock not released"
**Rešenje:**
```sql
-- U H2 Console
DELETE FROM DATABASECHANGELOGLOCK;
```

### Problem: "Admin user not found"
**Rešenje:**
1. Zaustavite aplikaciju
2. Obrišite bazu:
   ```cmd
   del data\bobcarrental.mv.db
   del data\bobcarrental.trace.db
   ```
3. Pokrenite ponovo: `restart-app.bat`

### Problem: "Compilation failed"
**Rešenje:**
```cmd
mvn clean install -DskipTests
```

## Dodatne Komande

### Samo Zaustavljanje
```cmd
taskkill /F /IM java.exe
```

### Samo Pokretanje (bez čišćenja)
```cmd
mvn spring-boot:run
```

### Build sa Testovima
```cmd
mvn clean install
```

### Build bez Testova
```cmd
mvn clean install -DskipTests
```

## Seed Podaci

Nakon restarta, baza sadrži:

### Admin Korisnik
- **Username:** admin
- **Password:** admin
- **Email:** admin@bobcarrental.com
- **Role:** ROLE_ADMIN
- **Status:** Enabled, Not Locked, Not Expired

### Role
1. **ROLE_ADMIN** - Pun pristup svim funkcijama
2. **ROLE_USER** - Ograničen pristup

## Sledeći Koraci

Nakon uspešnog restarta:

1. ✅ Pokrenite test skriptu: `.\test-api.ps1`
2. ✅ Testirajte API endpointe
3. ✅ Kreirajte dodatne seed podatke
4. ✅ Započnite frontend development

## Pomoć

Za dodatnu pomoć, pogledajte:
- `RUNTIME_FIXES_COMPLETE.md` - Sve runtime ispravke
- `COMPLETE_DATA_MODEL.md` - Specifikacija entiteta
- `test-api.ps1` - Test skripta sa primerima

---

**Made with Bob** 🚗