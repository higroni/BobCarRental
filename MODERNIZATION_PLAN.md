# Plan Modernizacije Alankar Aplikacije

## 📋 Pregled Projekta

Modernizacija legacy Clipper/Harbour aplikacije za upravljanje taxi flotom iz 1995. godine u modernu web aplikaciju sa:
- **Backend**: Java 21 + Spring Boot 3.x
- **Frontend**: Angular 17+
- **Baza**: H2 in-memory database
- **Security**: JWT autentifikacija sa role-based access control
- **Testing**: Automatizovani backend i E2E testovi sa Playwright

## 🎯 Ciljevi Projekta

1. ✅ Migracija svih podataka iz DBF fajlova u H2 bazu
2. ✅ Migracija sve poslovne logike, validacija i izračunavanja
3. ✅ Prebacivanje cenovnika iz FARES.TXT u bazu sa CRUD formama
4. ✅ Prebacivanje zaglavlja iz HEADER.TXT u bazu sa CRUD formama
5. ✅ Dodavanje upload funkcionalnosti za slike vozila
6. ✅ Implementacija security sistema (ADMIN/USER role)
7. ✅ Automatizovani backend testovi (JUnit, MockMvc)
8. ✅ Automatizovani E2E testovi sa Playwright
9. ✅ Priprema arhitekture za buduću mobilnu aplikaciju

## 👥 Korisničke Role

### ADMIN Role (admin/admin)
- Pristup svim modulima
- **Ekskluzivni pristup**:
  - Standard Fares Management (cenovnici)
  - Header Template Management (zaglavlja)

### USER Role (user/user)
- Pristup svim modulima osim:
  - Standard Fares (samo čitanje)
  - Header Templates (samo čitanje)

## 📊 Moduli za Migraciju

| Modul | Status | Opis |
|-------|--------|------|
| Client Master | ✅ Migrira se | Upravljanje klijentima |
| Vehicle Type Master | ✅ Migrira se | Tipovi vozila + slike |
| Booking | ✅ Migrira se | Rezervacije vozila |
| Trip Sheets | ✅ Migrira se | Potvrde narudžbi |
| Accounts Master | ✅ Migrira se | Računi i prijemi |
| Address Book | ✅ Migrira se | Adresar |
| Billing | ✅ Migrira se | Fakturisanje |
| Standard Fares | ✅ Migrira se | FARES.TXT → Baza |
| Header Templates | ✅ Migrira se | HEADER.TXT → Baza |
| Color Settings | ❌ Ne migrira se | Legacy funkcionalnost |

## 🏗️ Arhitektura

### Backend Stack
```
Java 21
├── Spring Boot 3.x
├── Spring Security (JWT + Role-based)
├── Spring Data JPA
├── H2 Database (in-memory)
├── Lombok
├── MapStruct
├── Liquibase
├── Swagger/OpenAPI
└── JUnit 5 + Mockito
```

### Frontend Stack
```
Angular 17+
├── Angular Material
├── RxJS
├── Reactive Forms
├── JWT Interceptors
├── Role Guards
└── Playwright (E2E testing)
```

### API Design (Mobile-Ready)
```
/api/v1/
├── /auth (login, logout, refresh, me)
├── /clients (CRUD + search)
├── /vehicles (CRUD + images)
├── /bookings (CRUD + date search)
├── /tripsheets (CRUD + scheduling)
├── /accounts (CRUD + reports)
├── /billing (generate invoices)
├── /addresses (CRUD)
├── /fares (ADMIN only)
├── /templates (ADMIN only)
├── /reports (PDF generation)
└── /health (monitoring)
```

## 📁 Database Schema

### Core Entities

#### User
```java
- id: Long
- username: String
- password: String (BCrypt)
- email: String
- roles: Set<Role>
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### Client
```java
- id: Long
- clientId: String (unique)
- clientName: String
- address1, address2, address3: String
- place: String
- city: String
- pinCode: String
- phone: String
- fax: String
- fare: Text (memo field)
- isSplit: Boolean
- tagged: Boolean
- createdAt, updatedAt: LocalDateTime
```

#### VehicleType
```java
- id: Long
- typeId: String (unique)
- typeName: String
- description: String
- images: List<VehicleImage>
- createdAt, updatedAt: LocalDateTime
```

#### VehicleImage
```java
- id: Long
- vehicleType: VehicleType
- imageName: String
- imageData: byte[] (@Lob)
- thumbnailData: byte[] (@Lob)
- contentType: String
- uploadDate: LocalDateTime
```

#### Booking
```java
- id: Long
- bookDate: LocalDate
- todayDate: LocalDate
- time: String
- ref: String
- typeId: String (FK to VehicleType)
- clientId: String (FK to Client)
- info1, info2, info3, info4: String
- tagged: Boolean
- createdAt, updatedAt: LocalDateTime
```

#### TripSheet
```java
- id: Long
- tripDate: LocalDate
- vehicleId: String
- driverId: String
- clientId: String (FK to Client)
- startKm: Integer
- endKm: Integer
- totalKm: Integer
- fare: BigDecimal
- remarks: String
- createdAt, updatedAt: LocalDateTime
```

#### Account
```java
- id: Long
- accountDate: LocalDate
- clientId: String (FK to Client)
- amount: BigDecimal
- type: String (RECEIPT/PAYMENT)
- remarks: String
- createdAt, updatedAt: LocalDateTime
```

#### StandardFare
```java
- id: Long
- vehicleTypeId: String (FK to VehicleType)
- fareType: String (PER_KM/PER_HOUR/FLAT)
- baseRate: BigDecimal
- perKmRate: BigDecimal
- perHourRate: BigDecimal
- minimumCharge: BigDecimal
- description: String
- effectiveFrom: LocalDate
- effectiveTo: LocalDate
- createdAt, updatedAt: LocalDateTime
```

#### HeaderTemplate
```java
- id: Long
- templateName: String
- templateType: String (INVOICE/TRIPSHEET/REPORT)
- headerText: Text
- footerText: Text
- variables: String (JSON)
- isActive: Boolean
- createdAt, updatedAt: LocalDateTime
```

## 🔐 Security Features

### JWT Authentication
- Access Token (15 min expiry)
- Refresh Token (7 days expiry)
- Token stored in HttpOnly cookies (web) / Local storage (mobile)

### Role-Based Access Control
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> createFare(@RequestBody FareRequest request) {
    // Only ADMIN can access
}

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public ResponseEntity<?> getClients() {
    // Both USER and ADMIN can access
}
```

### Password Security
- BCrypt hashing
- Minimum 8 characters
- Password change functionality

## 🧪 Testing Strategy

### Backend Tests (80%+ coverage)

#### Unit Tests
```java
@Test
void testClientValidation() {
    // Test PresenceChk validation
    // Test format validations
    // Test business rules
}

@Test
void testFareCalculation() {
    // Test per km calculation
    // Test per hour calculation
    // Test split rate logic
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerIntegrationTest {
    @Test
    void testCreateClient() {
        // Test full flow with database
    }
}
```

#### Security Tests
```java
@Test
void testAdminOnlyEndpoint() {
    // Test USER cannot access ADMIN endpoints
    // Test ADMIN can access all endpoints
}
```

### Frontend E2E Tests (Playwright)

```typescript
test('Admin can manage fares', async ({ page }) => {
  await page.goto('/login');
  await page.fill('[name="username"]', 'admin');
  await page.fill('[name="password"]', 'admin');
  await page.click('button[type="submit"]');
  
  await page.goto('/fares');
  await expect(page).toHaveURL('/fares');
  
  // Test CRUD operations
});

test('User cannot access fares', async ({ page }) => {
  await page.goto('/login');
  await page.fill('[name="username"]', 'user');
  await page.fill('[name="password"]', 'user');
  await page.click('button[type="submit"]');
  
  await page.goto('/fares');
  await expect(page).toHaveURL('/unauthorized');
});
```

## 📱 Mobile-Ready Features

### API Optimizations
1. **Pagination**: All list endpoints support pagination
2. **Filtering**: Advanced search capabilities
3. **DTOs**: Separate Summary and Detail DTOs
4. **Thumbnails**: Compressed images for mobile
5. **Versioning**: /api/v1/ for future compatibility
6. **CORS**: Configured for mobile apps
7. **Rate Limiting**: Protection against abuse

### Response Format
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  },
  "message": "Success",
  "timestamp": "2026-03-26T10:30:00Z"
}
```

### Error Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input",
    "details": [
      {
        "field": "clientId",
        "message": "Client ID already exists"
      }
    ]
  },
  "timestamp": "2026-03-26T10:30:00Z"
}
```

## 🚀 Deployment

### Docker Setup
```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JWT_SECRET=${JWT_SECRET}
    volumes:
      - ./data:/app/data
  
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

### Environment Variables
```properties
# Backend
SPRING_PROFILES_ACTIVE=dev|test|prod
JWT_SECRET=your-secret-key
JWT_EXPIRATION=900000
REFRESH_TOKEN_EXPIRATION=604800000
MAX_FILE_SIZE=5MB
ALLOWED_ORIGINS=http://localhost:4200,https://yourdomain.com

# Frontend
API_BASE_URL=http://localhost:8080/api/v1
```

## 📈 Timeline

| Faza | Trajanje | Opis |
|------|----------|------|
| Faza 1-2 | 5-7 dana | Analiza i Backend Setup |
| Faza 3-5 | 10-13 dana | Security, Entities, DTOs |
| Faza 6-7 | 7-9 dana | Services, Controllers, Migration |
| Faza 8-9 | 7-9 dana | Backend Tests, Frontend Setup |
| Faza 10-13 | 14-18 dana | Frontend Development |
| Faza 14-16 | 11-14 dana | E2E Tests, Integration, Polish |
| Faza 17-18 | 4-7 dana | Documentation, Deployment |
| **UKUPNO** | **55-70 dana** | **~3 meseca** |

## 📝 Validacije iz Legacy Sistema

### PresenceChk
```java
// Provera da li ClientId već postoji
if (clientRepository.existsByClientId(clientId)) {
    throw new ValidationException("Client ID already exists");
}
```

### CheckTime
```java
// Validacija formata vremena (HH:MM)
@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
private String time;
```

### SuperCheckIt
```java
// Provera da li referencirani entitet postoji
if (!vehicleTypeRepository.existsByTypeId(typeId)) {
    throw new ValidationException("Vehicle type not found");
}
```

### IsSplit Validation
```java
// Logika za podeljene tarife
if (client.getIsSplit() && booking.getMultipleVehicles()) {
    fare = calculateSplitFare(booking);
}
```

## 🔄 Data Migration Process

### 1. DBF Reading
```java
@Service
public class DbfMigrationService {
    public void migrateClients() {
        DBFReader reader = new DBFReader("CLIENT.DBF");
        while (reader.hasNext()) {
            Object[] row = reader.nextRecord();
            Client client = mapToClient(row);
            clientRepository.save(client);
        }
    }
}
```

### 2. FARES.TXT Parsing
```java
public List<StandardFare> parseFaresFile(String filePath) {
    List<StandardFare> fares = new ArrayList<>();
    // Parse text file format
    // Create StandardFare entities
    return fares;
}
```

### 3. HEADER.TXT Parsing
```java
public List<HeaderTemplate> parseHeaderFile(String filePath) {
    List<HeaderTemplate> templates = new ArrayList<>();
    // Parse text file format
    // Create HeaderTemplate entities
    return templates;
}
```

## 📚 Documentation Deliverables

1. ✅ README.md - Setup i pokretanje
2. ✅ API Documentation - Swagger UI
3. ✅ Postman Collection - API testiranje
4. ✅ User Manual - Korisnički priručnik
5. ✅ Technical Documentation - Arhitektura
6. ✅ Database Schema - ER dijagram
7. ✅ Migration Guide - Iz stare u novu aplikaciju
8. ✅ Mobile Integration Guide - Za budući projekat

## 🎨 UI/UX Features

### Responsive Design
- Mobile-first approach
- Breakpoints: 320px, 768px, 1024px, 1440px
- Touch-friendly controls
- Hamburger menu za mobile

### Accessibility
- WCAG 2.1 Level AA compliance
- Keyboard navigation
- Screen reader support
- High contrast mode

### Internationalization
- Srpski (default)
- Engleski
- Easy to add more languages

## 🔧 Development Tools

### Backend
- IntelliJ IDEA / Eclipse
- Maven / Gradle
- Postman
- H2 Console

### Frontend
- VS Code
- Angular CLI
- Chrome DevTools
- Playwright Test Runner

### DevOps
- Docker Desktop
- Git
- GitHub Actions
- SonarQube (code quality)

## 📞 Support & Maintenance

### Monitoring
- Application health checks
- Error logging (Logback)
- Performance metrics
- API usage statistics

### Backup Strategy
- Daily database backups
- Image storage backups
- Configuration backups
- Automated backup verification

## 🎯 Success Criteria

- ✅ Svi moduli migrirani i funkcionalni
- ✅ Sve validacije implementirane
- ✅ Backend test coverage > 80%
- ✅ Svi E2E testovi prolaze
- ✅ API dokumentacija kompletna
- ✅ Responsive design na svim uređajima
- ✅ Security testovi prolaze
- ✅ Performance: < 2s page load
- ✅ Mobile-ready API
- ✅ Zero critical bugs

## 📄 License

Original application: © 1995 T.N.C.Venkata Rangan
Modernized version: [Your License Here]

---

**Verzija**: 1.0  
**Datum**: 26.03.2026  
**Autor**: Bob (AI Software Engineer)