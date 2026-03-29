# 🚀 Complete Implementation Guide - BobCarRental Migration

## ✅ Šta je Završeno (Completed)

### 1. Models (14 entities) ✅
- BaseEntity, User, Role, Client, VehicleType, VehicleImage
- Booking, TripSheet, Billing, Account, Address
- StandardFare, HeaderTemplate

### 2. Repositories (12 repositories) ✅
- UserRepository, RoleRepository, ClientRepository
- VehicleTypeRepository, VehicleImageRepository
- BookingRepository, TripSheetRepository, BillingRepository
- AccountRepository, AddressRepository
- StandardFareRepository, HeaderTemplateRepository

### 3. Common DTOs (4 files) ✅
- ApiResponse, ErrorDetails, FieldError, PageResponse

### 4. Auth DTOs (2 files) ✅
- LoginRequest, AuthResponse

### 5. Client Module DTOs (4 files) ✅
- ClientRequest, ClientResponse, ClientSummaryResponse, ClientMapper

### 6. Booking Module DTOs (4 files) ✅
- BookingRequest, BookingResponse, BookingSummaryResponse, BookingMapper

### 7. TripSheet Module DTOs (4 files) ✅
- TripSheetRequest, TripSheetResponse, TripSheetSummaryResponse, TripSheetMapper

### 8. Security Infrastructure (5 files) ✅
- JwtTokenProvider
- JwtAuthenticationFilter
- JwtAuthenticationEntryPoint
- CustomUserDetailsService
- SecurityConfig

### 9. Exception Handling (3 files) ✅
- ResourceNotFoundException
- ValidationException
- GlobalExceptionHandler

### 10. Services (2 files) ✅
- AuthService + AuthServiceImpl
- ClientService + ClientServiceImpl

### 11. Controllers (2 files) ✅
- AuthController
- ClientController (treba kreirati)

---

## 📋 Šta Treba Uraditi (TODO)

### FAZA 1: Završi Backend (Prioritet: VISOK)

#### 1.1 Kreiraj ClientController
```bash
# Lokacija: bobcarrental/backend/src/main/java/com/bobcarrental/controller/ClientController.java
# Kopiraj iz COMPLETE_MODULE_TEMPLATE.md
```

#### 1.2 Kreiraj Preostale DTOs (41 fajl)

**Billing Module (3 fajla):**
- BillingResponse.java
- BillingSummaryResponse.java
- BillingMapper.java

**VehicleType Module (4 fajla):**
- VehicleTypeRequest.java
- VehicleTypeResponse.java
- VehicleTypeSummaryResponse.java
- VehicleTypeMapper.java

**VehicleImage Module (4 fajla):**
- VehicleImageRequest.java
- VehicleImageResponse.java
- VehicleImageSummaryResponse.java
- VehicleImageMapper.java

**Account Module (4 fajla):**
- AccountRequest.java
- AccountResponse.java
- AccountSummaryResponse.java
- AccountMapper.java

**Address Module (4 fajla):**
- AddressRequest.java
- AddressResponse.java
- AddressSummaryResponse.java
- AddressMapper.java

**StandardFare Module (4 fajla):**
- StandardFareRequest.java
- StandardFareResponse.java
- StandardFareSummaryResponse.java
- StandardFareMapper.java

**HeaderTemplate Module (4 fajla):**
- HeaderTemplateRequest.java
- HeaderTemplateResponse.java
- HeaderTemplateSummaryResponse.java
- HeaderTemplateMapper.java

**User Module (4 fajla):**
- UserRequest.java
- UserResponse.java
- UserSummaryResponse.java
- UserMapper.java

**Role Module (4 fajla):**
- RoleRequest.java
- RoleResponse.java
- RoleSummaryResponse.java
- RoleMapper.java

#### 1.3 Kreiraj Service Layer (18 fajlova)

**Service Interfaces:**
- BillingService.java
- VehicleTypeService.java
- VehicleImageService.java
- BookingService.java
- TripSheetService.java
- AccountService.java
- AddressService.java
- StandardFareService.java
- HeaderTemplateService.java

**Service Implementations:**
- BillingServiceImpl.java
- VehicleTypeServiceImpl.java
- VehicleImageServiceImpl.java
- BookingServiceImpl.java
- TripSheetServiceImpl.java
- AccountServiceImpl.java
- AddressServiceImpl.java
- StandardFareServiceImpl.java
- HeaderTemplateServiceImpl.java

#### 1.4 Kreiraj FareCalculationService (KRITIČNO!)

**Fajl**: `FareCalculationService.java`

```java
package com.bobcarrental.service;

import java.math.BigDecimal;
import java.time.LocalTime;

public interface FareCalculationService {
    
    /**
     * Calculate local fare (LocFare from legacy)
     * Formula: (km * rate) + (hours * hourly_rate)
     */
    BigDecimal calculateLocalFare(
        int kilometers,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal kmRate,
        BigDecimal hourlyRate
    );
    
    /**
     * Calculate outstation fare (OutFare from legacy)
     * Formula: (km * rate) + driver_allowance + night_halt
     */
    BigDecimal calculateOutstationFare(
        int kilometers,
        int days,
        BigDecimal kmRate,
        BigDecimal driverAllowance,
        BigDecimal nightHalt
    );
    
    /**
     * Convert time string to decimal hours (Time2Val from legacy)
     * Example: "02:30" -> 2.5
     */
    double timeToDecimal(LocalTime time);
    
    /**
     * Calculate hours between two times
     */
    double calculateHours(LocalTime start, LocalTime end);
}
```

#### 1.5 Kreiraj Controllers (9 fajlova)

- BillingController.java
- VehicleTypeController.java
- VehicleImageController.java
- BookingController.java
- TripSheetController.java
- AccountController.java
- AddressController.java
- StandardFareController.java (ADMIN only)
- HeaderTemplateController.java (ADMIN only)

#### 1.6 Liquibase Migrations (11 fajlova)

**Lokacija**: `bobcarrental/backend/src/main/resources/db/changelog/`

```
db.changelog-master.yaml
changes/
  001-create-roles-table.sql
  002-create-users-table.sql
  003-create-user-roles-table.sql
  004-create-clients-table.sql
  005-create-vehicle-types-table.sql
  006-create-vehicle-images-table.sql
  007-create-bookings-table.sql
  008-create-trip-sheets-table.sql
  009-create-billings-table.sql
  010-create-accounts-table.sql
  011-create-addresses-table.sql
  012-create-standard-fares-table.sql
  013-create-header-templates-table.sql
  014-seed-roles.sql
  015-seed-admin-user.sql
```

#### 1.7 Update application.properties

```properties
# JWT Configuration
app.jwt.secret=YourVeryLongSecretKeyForJWTTokenGenerationMinimum512Bits
app.jwt.expiration-ms=86400000
app.jwt.refresh-expiration-ms=604800000

# Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
spring.liquibase.enabled=true
```

---

### FAZA 2: Angular Frontend (Prioritet: SREDNJI)

#### 2.1 Setup Angular Project

```bash
cd bobcarrental/frontend
ng new frontend --routing --style=scss
cd frontend
npm install @angular/material @angular/cdk
npm install @auth0/angular-jwt
npm install chart.js ng2-charts
npm install ngx-toastr
```

#### 2.2 Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── admin.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── jwt.interceptor.ts
│   │   │   │   └── error.interceptor.ts
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   └── storage.service.ts
│   │   │   └── models/
│   │   │       ├── user.model.ts
│   │   │       ├── auth-response.model.ts
│   │   │       └── api-response.model.ts
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── navbar/
│   │   │   │   ├── sidebar/
│   │   │   │   ├── loading-spinner/
│   │   │   │   └── confirmation-dialog/
│   │   │   ├── directives/
│   │   │   └── pipes/
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   │   ├── login/
│   │   │   │   └── auth.module.ts
│   │   │   ├── dashboard/
│   │   │   ├── clients/
│   │   │   │   ├── client-list/
│   │   │   │   ├── client-form/
│   │   │   │   ├── client-detail/
│   │   │   │   ├── clients.service.ts
│   │   │   │   └── clients.module.ts
│   │   │   ├── bookings/
│   │   │   ├── trip-sheets/
│   │   │   ├── billings/
│   │   │   ├── vehicles/
│   │   │   ├── accounts/
│   │   │   ├── addresses/
│   │   │   ├── standard-fares/ (ADMIN)
│   │   │   └── header-templates/ (ADMIN)
│   │   └── app.component.ts
│   └── environments/
│       ├── environment.ts
│       └── environment.prod.ts
```

#### 2.3 Kritični Fajlovi za Frontend

**auth.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const user = localStorage.getItem('currentUser');
    if (user) this.currentUserSubject.next(JSON.parse(user));
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auth/login`, { username, password })
      .pipe(tap(response => {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        localStorage.setItem('currentUser', JSON.stringify(response.data));
        this.currentUserSubject.next(response.data);
      }));
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.includes(role) || false;
  }
}
```

**jwt.interceptor.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('accessToken');
    
    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    
    return next.handle(request);
  }
}
```

**auth.guard.ts:**
```typescript
import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
```

**clients.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClientsService {
  private apiUrl = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get(this.apiUrl, { params });
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  create(client: any): Observable<any> {
    return this.http.post(this.apiUrl, client);
  }

  update(id: number, client: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, client);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  search(query: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search`, {
      params: { query }
    });
  }
}
```

---

## 🎯 Sledeći Koraci (Step by Step)

### Korak 1: Završi ClientController
```bash
# Kopiraj iz COMPLETE_MODULE_TEMPLATE.md
# Lokacija: bobcarrental/backend/src/main/java/com/bobcarrental/controller/ClientController.java
```

### Korak 2: Kreiraj Preostale DTOs
Za svaki modul (Billing, VehicleType, VehicleImage, Account, Address, StandardFare, HeaderTemplate):

1. **Request DTO** - sa validacijama
2. **Response DTO** - sa svim poljima
3. **SummaryResponse DTO** - sa osnovnim poljima
4. **Mapper** - MapStruct interfejs

**Pattern:**
```java
// {Entity}Request.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {Entity}Request {
    @NotBlank(message = "Field is required")
    @Size(max = 50)
    private String field;
}

// {Entity}Response.java
@Data
@Builder
public class {Entity}Response {
    private Long id;
    private String field;
    private LocalDateTime createdAt;
}

// {Entity}SummaryResponse.java
@Data
@Builder
public class {Entity}SummaryResponse {
    private Long id;
    private String field;
}

// {Entity}Mapper.java
@Mapper(componentModel = "spring")
public interface {Entity}Mapper {
    {Entity} toEntity({Entity}Request request);
    {Entity}Response toResponse({Entity} entity);
    {Entity}SummaryResponse toSummaryResponse({Entity} entity);
    void updateEntityFromRequest({Entity}Request request, @MappingTarget {Entity} entity);
    List<{Entity}SummaryResponse> toSummaryResponseList(List<{Entity}> entities);
}
```

### Korak 3: Kreiraj Service Layer
Za svaki modul:

1. **Service Interface** - definicija metoda
2. **Service Implementation** - biznis logika

**Pattern** (kopiraj iz ClientService/ClientServiceImpl)

### Korak 4: Kreiraj Controllers
Za svaki modul:

**Pattern** (kopiraj iz ClientController u COMPLETE_MODULE_TEMPLATE.md)

### Korak 5: Liquibase Migrations

**db.changelog-master.yaml:**
```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-roles-table.sql
  - include:
      file: db/changelog/changes/002-create-users-table.sql
  # ... ostale migracije
```

**001-create-roles-table.sql:**
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**014-seed-roles.sql:**
```sql
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access'),
('USER', 'Regular user with limited access');
```

**015-seed-admin-user.sql:**
```sql
-- Password: admin123 (BCrypt hashed)
INSERT INTO users (username, password, full_name, email, active) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'admin@bobcarrental.com', true);

INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN'));
```

### Korak 6: Angular Frontend Setup

```bash
cd bobcarrental
mkdir frontend
cd frontend
ng new frontend --routing --style=scss --skip-git
cd frontend

# Install dependencies
npm install @angular/material @angular/cdk
npm install @auth0/angular-jwt
npm install ngx-toastr
npm install chart.js ng2-charts

# Generate modules
ng generate module core
ng generate module shared
ng generate module features/auth --routing
ng generate module features/dashboard --routing
ng generate module features/clients --routing
ng generate module features/bookings --routing
ng generate module features/trip-sheets --routing

# Generate services
ng generate service core/services/auth
ng generate service core/services/storage
ng generate service features/clients/clients

# Generate guards
ng generate guard core/guards/auth
ng generate guard core/guards/admin

# Generate interceptors
ng generate interceptor core/interceptors/jwt
ng generate interceptor core/interceptors/error

# Generate components
ng generate component features/auth/login
ng generate component features/dashboard/dashboard
ng generate component features/clients/client-list
ng generate component features/clients/client-form
ng generate component shared/components/navbar
ng generate component shared/components/sidebar
```

### Korak 7: Environment Configuration

**environment.ts:**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### Korak 8: App Routing

**app-routing.module.ts:**
```typescript
const routes: Routes = [
  { path: 'login', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { 
    path: '', 
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule) },
      { path: 'clients', loadChildren: () => import('./features/clients/clients.module').then(m => m.ClientsModule) },
      { path: 'bookings', loadChildren: () => import('./features/bookings/bookings.module').then(m => m.BookingsModule) },
      // ... ostali moduli
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
```

---

## 🔥 Kritični Algoritmi iz Legacy Sistema

### 1. LocFare (Local Fare Calculation)
```java
public BigDecimal calculateLocalFare(int km, LocalTime start, LocalTime end, 
                                     BigDecimal kmRate, BigDecimal hourlyRate) {
    // km * rate
    BigDecimal kmCost = BigDecimal.valueOf(km).multiply(kmRate);
    
    // hours * hourly_rate
    double hours = calculateHours(start, end);
    BigDecimal hourCost = BigDecimal.valueOf(hours).multiply(hourlyRate);
    
    return kmCost.add(hourCost);
}
```

### 2. OutFare (Outstation Fare Calculation)
```java
public BigDecimal calculateOutstationFare(int km, int days, BigDecimal kmRate,
                                          BigDecimal driverAllowance, BigDecimal nightHalt) {
    // km * rate
    BigDecimal kmCost = BigDecimal.valueOf(km).multiply(kmRate);
    
    // driver allowance * days
    BigDecimal allowanceCost = driverAllowance.multiply(BigDecimal.valueOf(days));
    
    // night halt * (days - 1)
    BigDecimal haltCost = nightHalt.multiply(BigDecimal.valueOf(Math.max(0, days - 1)));
    
    return kmCost.add(allowanceCost).add(haltCost);
}
```

### 3. Time2Val (Time to Decimal Hours)
```java
public double timeToDecimal(LocalTime time) {
    return time.getHour() + (time.getMinute() / 60.0);
}

public double calculateHours(LocalTime start, LocalTime end) {
    double startDecimal = timeToDecimal(start);
    double endDecimal = timeToDecimal(end);
    
    // Handle overnight trips
    if (endDecimal < startDecimal) {
        endDecimal += 24;
    }
    
    return endDecimal - startDecimal;
}
```

---

## 📊 Statistika Implementacije

### Završeno:
- **Models**: 14/14 (100%)
- **Repositories**: 12/12 (100%)
- **Common DTOs**: 4/4 (100%)
- **Auth DTOs**: 2/2 (100%)
- **Module DTOs**: 12/60 (20%)
- **Security**: 5/5 (100%)
- **Exception Handling**: 3/3 (100%)
- **Services**: 2/20 (10%)
- **Controllers**: 1/10 (10%)

### Preostalo:
- **Module DTOs**: 48 fajlova
- **Services**: 18 fajlova
- **Controllers**: 9 fajlova
- **Liquibase**: 15 fajlova
- **Frontend**: ~150 fajlova

**Ukupno preostalo**: ~240 fajlova

---

## 🚀 Kako Nastaviti

### Opcija 1: Automatska Generacija (Preporučeno)
Koristite template pattern i kreirajte bash/PowerShell skriptu koja generiše sve fajlove:

```bash
# generate-module.sh
MODULE_NAME=$1
# Generiše sve fajlove za modul koristeći template
```

### Opcija 2: Ručno Kreiranje
Kopirajte Client modul kao template i prilagodite za svaki entitet.

### Opcija 3: AI Asistent
Nastavite sa AI asistentom korak po korak, modul po modul.

---

## 📚 Dokumentacija

- **COMPLETE_MODULE_TEMPLATE.md** - Kompletan primer Client modula
- **IMPLEMENTATION_PLAN.md** - Detaljan plan sa arhitekturom
- **ARCHITECTURE_DIAGRAMS.md** - Dijagrami sistema
- **LEGACY_ANALYSIS.md** - Analiza legacy sistema

---

## ✅ Testiranje

### Backend Tests
```bash
cd bobcarrental/backend
mvn test
```

### Frontend Tests
```bash
cd bobcarrental/frontend
ng test
ng e2e
```

### Integration Tests
```bash
# Pokreni backend
cd bobcarrental/backend
mvn spring-boot:run

# Pokreni frontend
cd bobcarrental/frontend
ng serve

# Otvori: http://localhost:4200
# Login: admin / admin123
```

---

## 🐳 Docker Deployment

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
  
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

---

## 📞 Podrška

Za pitanja ili probleme, konsultujte:
1. COMPLETE_MODULE_TEMPLATE.md - Kompletan primer
2. IMPLEMENTATION_PLAN.md - Detaljan plan
3. Spring Boot dokumentaciju
4. Angular dokumentaciju

**Srećno sa implementacijom! 🎉**