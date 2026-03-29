# Arhitekturni Dijagrami - AS-IS vs TO-BE

## 📊 AS-IS Arhitektura (Legacy - 1995)

### Sistemski Pregled

```mermaid
graph TB
    subgraph "MS-DOS Environment"
        USER[👤 Single User<br/>Operator]
        
        subgraph "Alankar.exe<br/>(Clipper 5.x)"
            MAIN[MAIN.PRG<br/>Menu System]
            
            subgraph "UI Layer"
                FORM[Form_Cls.PRG<br/>UI Framework]
                MENU[Menu_Cls.PRG]
                MSG[Msg_Cls.PRG]
                LIST[List_Cls.PRG]
                CLR[Clr_Cls.PRG<br/>Color Settings]
            end
            
            subgraph "Business Logic"
                CLIENT[CLIENT.PRG<br/>Customer Mgmt]
                VEHTYPE[VEHTYPE.PRG<br/>Vehicle Types]
                BOOKING[BOOKING.PRG<br/>Reservations]
                TRIPMAS[TRIPMAS.PRG<br/>Trip Sheets]
                ACCOUNTS[ACCOUNTS.PRG<br/>Financial]
                BILLER[BILLER.PRG<br/>Invoicing]
                ADDRESS[ADDRESS.PRG<br/>Address Book]
            end
            
            subgraph "Utilities"
                STDFARES[STDFARES.PRG<br/>Fare Parser]
                HEADING[HEADING.PRG<br/>Header Parser]
                SYSCLS[SYS_CLS.PRG<br/>System Utils]
                PRNCLS[PRN_CLS.PRG<br/>Printing]
            end
        end
        
        subgraph "Data Storage"
            DBF1[(CLIENT.DBF<br/>Customer Data)]
            DBF2[(VEHTYPE.DBF<br/>Vehicle Types)]
            DBF3[(BOOKING.DBF<br/>Bookings)]
            DBF4[(TRPSHEET.DBF<br/>Trip Sheets)]
            DBF5[(ACCOUNTS.DBF<br/>Accounts)]
            DBF6[(ADDRESS.DBF<br/>Addresses)]
            DBF7[(BILLING.DBF<br/>Bills)]
            
            TXT1[FARES.TXT<br/>Rate Card]
            TXT2[HEADER.TXT<br/>Templates]
            TXT3[COLORS.DBF<br/>UI Colors]
        end
        
        PRINTER[🖨️ Dot Matrix<br/>Printer]
    end
    
    USER -->|Keyboard Input| MAIN
    MAIN --> FORM
    MAIN --> MENU
    
    FORM --> CLIENT
    FORM --> VEHTYPE
    FORM --> BOOKING
    FORM --> TRIPMAS
    FORM --> ACCOUNTS
    FORM --> BILLER
    FORM --> ADDRESS
    
    CLIENT -.->|Read/Write| DBF1
    VEHTYPE -.->|Read/Write| DBF2
    BOOKING -.->|Read/Write| DBF3
    TRIPMAS -.->|Read/Write| DBF4
    ACCOUNTS -.->|Read/Write| DBF5
    ADDRESS -.->|Read/Write| DBF6
    BILLER -.->|Read/Write| DBF7
    
    STDFARES -.->|Parse| TXT1
    HEADING -.->|Parse| TXT2
    CLR -.->|Read/Write| TXT3
    
    PRNCLS -->|Print| PRINTER
    
    style USER fill:#e1f5ff
    style MAIN fill:#fff3cd
    style PRINTER fill:#f8d7da
```

### Data Flow - AS-IS

```mermaid
flowchart LR
    subgraph "Input"
        KB[⌨️ Keyboard]
    end
    
    subgraph "Processing"
        APP[Alankar.exe<br/>Single Process]
        VAL[Built-in<br/>Validations]
        CALC[Fare<br/>Calculations]
    end
    
    subgraph "Storage"
        DBF[(DBF Files<br/>Local Disk)]
        TXT[Text Files<br/>Config]
    end
    
    subgraph "Output"
        SCREEN[📺 CRT Monitor<br/>Text Mode]
        PRINT[🖨️ Printer<br/>Reports]
    end
    
    KB -->|User Input| APP
    APP -->|Validate| VAL
    VAL -->|Calculate| CALC
    CALC -->|Save| DBF
    CALC -->|Read Config| TXT
    APP -->|Display| SCREEN
    APP -->|Generate| PRINT
    
    style APP fill:#fff3cd
    style DBF fill:#d1ecf1
    style SCREEN fill:#d4edda
    style PRINT fill:#f8d7da
```

### Limitations - AS-IS

```mermaid
mindmap
  root((AS-IS<br/>Limitations))
    Single User
      No Concurrency
      One Operator
      No Collaboration
    Platform
      MS-DOS Only
      16-bit
      No Networking
    Data
      DBF Format
      No Backup
      File Corruption Risk
      No Transactions
    UI
      Text Mode
      No Graphics
      Fixed Colors
      Keyboard Only
    Security
      No Authentication
      No Authorization
      No Audit Trail
    Scalability
      Limited Records
      Slow Searches
      No Indexing
    Integration
      No APIs
      No Export
      Manual Reports
```

---

## 🚀 TO-BE Arhitektura (Modern - 2026)

### Sistemski Pregled

```mermaid
graph TB
    subgraph "Client Layer"
        WEB[🌐 Web Browser<br/>Angular 17+]
        MOBILE[📱 Mobile App<br/>Future]
    end
    
    subgraph "API Gateway"
        NGINX[NGINX<br/>Reverse Proxy]
    end
    
    subgraph "Backend - Spring Boot 3.x"
        subgraph "Security Layer"
            JWT[JWT Filter<br/>Authentication]
            RBAC[Role-Based<br/>Access Control]
        end
        
        subgraph "API Layer - /api/v1/"
            AUTH_API[AuthController<br/>Login/Logout]
            CLIENT_API[ClientController<br/>CRUD]
            VEHICLE_API[VehicleController<br/>CRUD + Images]
            BOOKING_API[BookingController<br/>CRUD + Search]
            TRIP_API[TripSheetController<br/>Scheduling]
            ACCOUNT_API[AccountController<br/>Financial]
            BILLING_API[BillingController<br/>Invoicing]
            FARE_API[FareController<br/>ADMIN Only]
            TEMPLATE_API[TemplateController<br/>ADMIN Only]
            REPORT_API[ReportController<br/>PDF Gen]
        end
        
        subgraph "Service Layer"
            AUTH_SVC[AuthService<br/>JWT Management]
            CLIENT_SVC[ClientService<br/>Business Logic]
            VEHICLE_SVC[VehicleService<br/>+ Image Service]
            BOOKING_SVC[BookingService<br/>Validations]
            TRIP_SVC[TripSheetService<br/>Scheduling]
            ACCOUNT_SVC[AccountService<br/>Financial]
            BILLING_SVC[BillingService<br/>Calculations]
            FARE_SVC[FareService<br/>Rate Management]
            TEMPLATE_SVC[TemplateService<br/>Templates]
            REPORT_SVC[ReportService<br/>PDF Generation]
            VALIDATION_SVC[ValidationService<br/>All Validations]
        end
        
        subgraph "Data Layer"
            REPO[Spring Data JPA<br/>Repositories]
            MAPPER[MapStruct<br/>DTO Mappers]
        end
    end
    
    subgraph "Database"
        H2[(H2 Database<br/>In-Memory)]
        
        subgraph "Tables"
            T_USER[users]
            T_ROLE[roles]
            T_CLIENT[clients]
            T_VEHICLE[vehicle_types]
            T_IMAGE[vehicle_images<br/>BLOB]
            T_BOOKING[bookings]
            T_TRIP[trip_sheets]
            T_ACCOUNT[accounts]
            T_FARE[standard_fares]
            T_TEMPLATE[header_templates]
        end
    end
    
    subgraph "External Services"
        SWAGGER[📖 Swagger UI<br/>API Docs]
        ACTUATOR[💓 Actuator<br/>Health Checks]
    end
    
    WEB -->|HTTPS| NGINX
    MOBILE -.->|HTTPS| NGINX
    NGINX -->|Forward| JWT
    
    JWT --> RBAC
    RBAC --> AUTH_API
    RBAC --> CLIENT_API
    RBAC --> VEHICLE_API
    RBAC --> BOOKING_API
    RBAC --> TRIP_API
    RBAC --> ACCOUNT_API
    RBAC --> BILLING_API
    RBAC --> FARE_API
    RBAC --> TEMPLATE_API
    RBAC --> REPORT_API
    
    AUTH_API --> AUTH_SVC
    CLIENT_API --> CLIENT_SVC
    VEHICLE_API --> VEHICLE_SVC
    BOOKING_API --> BOOKING_SVC
    TRIP_API --> TRIP_SVC
    ACCOUNT_API --> ACCOUNT_SVC
    BILLING_API --> BILLING_SVC
    FARE_API --> FARE_SVC
    TEMPLATE_API --> TEMPLATE_SVC
    REPORT_API --> REPORT_SVC
    
    CLIENT_SVC --> VALIDATION_SVC
    BOOKING_SVC --> VALIDATION_SVC
    BILLING_SVC --> FARE_SVC
    
    AUTH_SVC --> REPO
    CLIENT_SVC --> REPO
    VEHICLE_SVC --> REPO
    BOOKING_SVC --> REPO
    TRIP_SVC --> REPO
    ACCOUNT_SVC --> REPO
    BILLING_SVC --> REPO
    FARE_SVC --> REPO
    TEMPLATE_SVC --> REPO
    
    REPO --> MAPPER
    MAPPER --> H2
    
    H2 --> T_USER
    H2 --> T_ROLE
    H2 --> T_CLIENT
    H2 --> T_VEHICLE
    H2 --> T_IMAGE
    H2 --> T_BOOKING
    H2 --> T_TRIP
    H2 --> T_ACCOUNT
    H2 --> T_FARE
    H2 --> T_TEMPLATE
    
    SWAGGER -.->|Document| AUTH_API
    ACTUATOR -.->|Monitor| REPO
    
    style WEB fill:#e1f5ff
    style MOBILE fill:#e1f5ff
    style JWT fill:#fff3cd
    style RBAC fill:#fff3cd
    style H2 fill:#d1ecf1
    style SWAGGER fill:#d4edda
    style ACTUATOR fill:#d4edda
```

### Data Flow - TO-BE

```mermaid
flowchart TB
    subgraph "Client"
        USER[👤 User<br/>admin/user]
        BROWSER[🌐 Browser<br/>Angular App]
    end
    
    subgraph "Authentication"
        LOGIN[Login Form]
        JWT_TOKEN[JWT Token<br/>+ Refresh Token]
    end
    
    subgraph "API Request Flow"
        REQUEST[HTTP Request<br/>+ JWT Header]
        VALIDATE[Validate Token]
        AUTHORIZE[Check Role<br/>ADMIN/USER]
        CONTROLLER[Controller<br/>Endpoint]
    end
    
    subgraph "Business Logic"
        SERVICE[Service Layer<br/>Business Rules]
        VALIDATION[Validation<br/>PresenceChk<br/>CheckTime<br/>SuperCheckIt]
        CALCULATION[Calculations<br/>Fares<br/>Split Rate]
    end
    
    subgraph "Data Access"
        REPOSITORY[JPA Repository]
        MAPPER[MapStruct<br/>DTO Mapping]
        DATABASE[(H2 Database<br/>Transactional)]
    end
    
    subgraph "Response"
        DTO[Response DTO<br/>JSON]
        SUCCESS[Success<br/>200/201]
        ERROR[Error<br/>400/401/403]
    end
    
    USER -->|Interact| BROWSER
    BROWSER -->|POST /login| LOGIN
    LOGIN -->|Credentials| JWT_TOKEN
    JWT_TOKEN -->|Store| BROWSER
    
    BROWSER -->|API Call| REQUEST
    REQUEST --> VALIDATE
    VALIDATE -->|Valid?| AUTHORIZE
    AUTHORIZE -->|Authorized?| CONTROLLER
    
    CONTROLLER --> SERVICE
    SERVICE --> VALIDATION
    VALIDATION -->|Valid?| CALCULATION
    CALCULATION --> REPOSITORY
    
    REPOSITORY --> MAPPER
    MAPPER --> DATABASE
    DATABASE -->|Result| MAPPER
    MAPPER -->|Entity to DTO| DTO
    
    DTO -->|Success| SUCCESS
    VALIDATION -->|Invalid| ERROR
    AUTHORIZE -->|Forbidden| ERROR
    
    SUCCESS --> BROWSER
    ERROR --> BROWSER
    
    style USER fill:#e1f5ff
    style JWT_TOKEN fill:#fff3cd
    style DATABASE fill:#d1ecf1
    style SUCCESS fill:#d4edda
    style ERROR fill:#f8d7da
```

### Security Architecture

```mermaid
graph TB
    subgraph "User Access"
        ADMIN[👤 ADMIN<br/>admin/admin]
        USER[👤 USER<br/>user/user]
    end
    
    subgraph "Authentication"
        LOGIN[Login Endpoint<br/>/api/v1/auth/login]
        JWT_GEN[JWT Generator<br/>Access + Refresh]
        BCRYPT[BCrypt<br/>Password Hash]
    end
    
    subgraph "Authorization"
        JWT_FILTER[JWT Filter<br/>Validate Token]
        ROLE_CHECK[@PreAuthorize<br/>Role Check]
        
        subgraph "ADMIN Only"
            FARE_CTRL[Fare Controller]
            TEMPLATE_CTRL[Template Controller]
        end
        
        subgraph "USER + ADMIN"
            CLIENT_CTRL[Client Controller]
            VEHICLE_CTRL[Vehicle Controller]
            BOOKING_CTRL[Booking Controller]
            TRIP_CTRL[Trip Controller]
            ACCOUNT_CTRL[Account Controller]
            BILLING_CTRL[Billing Controller]
        end
    end
    
    subgraph "Database"
        USER_TABLE[(users table<br/>password_hash)]
        ROLE_TABLE[(roles table<br/>ADMIN/USER)]
    end
    
    ADMIN -->|Login| LOGIN
    USER -->|Login| LOGIN
    LOGIN --> BCRYPT
    BCRYPT -->|Verify| USER_TABLE
    USER_TABLE -->|Load Roles| ROLE_TABLE
    ROLE_TABLE --> JWT_GEN
    JWT_GEN -->|Return Token| ADMIN
    JWT_GEN -->|Return Token| USER
    
    ADMIN -->|API Request<br/>+ JWT| JWT_FILTER
    USER -->|API Request<br/>+ JWT| JWT_FILTER
    
    JWT_FILTER -->|Extract Role| ROLE_CHECK
    
    ROLE_CHECK -->|hasRole('ADMIN')| FARE_CTRL
    ROLE_CHECK -->|hasRole('ADMIN')| TEMPLATE_CTRL
    
    ROLE_CHECK -->|hasAnyRole| CLIENT_CTRL
    ROLE_CHECK -->|hasAnyRole| VEHICLE_CTRL
    ROLE_CHECK -->|hasAnyRole| BOOKING_CTRL
    ROLE_CHECK -->|hasAnyRole| TRIP_CTRL
    ROLE_CHECK -->|hasAnyRole| ACCOUNT_CTRL
    ROLE_CHECK -->|hasAnyRole| BILLING_CTRL
    
    style ADMIN fill:#fff3cd
    style USER fill:#e1f5ff
    style JWT_GEN fill:#d4edda
    style FARE_CTRL fill:#f8d7da
    style TEMPLATE_CTRL fill:#f8d7da
```

### Database Schema - TO-BE

```mermaid
erDiagram
    users ||--o{ user_roles : has
    roles ||--o{ user_roles : assigned_to
    
    clients ||--o{ bookings : makes
    clients ||--o{ trip_sheets : has
    clients ||--o{ accounts : owns
    clients ||--o{ billings : receives
    
    vehicle_types ||--o{ vehicle_images : has
    vehicle_types ||--o{ bookings : requested_in
    vehicle_types ||--o{ trip_sheets : assigned_to
    vehicle_types ||--o{ standard_fares : has_rates
    
    bookings ||--|| trip_sheets : confirmed_as
    
    trip_sheets ||--o{ billings : generates
    
    users {
        bigint id PK
        string username UK
        string password_hash
        string email
        timestamp created_at
        timestamp updated_at
    }
    
    roles {
        bigint id PK
        string name UK
        string description
    }
    
    user_roles {
        bigint user_id FK
        bigint role_id FK
    }
    
    clients {
        bigint id PK
        string client_id UK
        string client_name
        string address1
        string address2
        string address3
        string place
        string city
        string pin_code
        string phone
        string fax
        text fare_memo
        boolean is_split
        boolean tagged
        timestamp created_at
        timestamp updated_at
    }
    
    vehicle_types {
        bigint id PK
        string type_id UK
        string type_name
        string description
        timestamp created_at
        timestamp updated_at
    }
    
    vehicle_images {
        bigint id PK
        bigint vehicle_type_id FK
        string image_name
        blob image_data
        blob thumbnail_data
        string content_type
        timestamp upload_date
    }
    
    bookings {
        bigint id PK
        date book_date
        date today_date
        string time
        string ref
        string type_id FK
        string client_id FK
        string info1
        string info2
        string info3
        string info4
        boolean tagged
        timestamp created_at
        timestamp updated_at
    }
    
    trip_sheets {
        bigint id PK
        date trip_date
        string vehicle_id
        string driver_id
        string client_id FK
        integer start_km
        integer end_km
        integer total_km
        decimal fare
        string remarks
        timestamp created_at
        timestamp updated_at
    }
    
    accounts {
        bigint id PK
        date account_date
        string client_id FK
        decimal amount
        string type
        string remarks
        timestamp created_at
        timestamp updated_at
    }
    
    billings {
        bigint id PK
        string client_id FK
        date billing_date
        decimal amount
        string status
        text details
        timestamp created_at
        timestamp updated_at
    }
    
    standard_fares {
        bigint id PK
        string vehicle_type_id FK
        string fare_type
        decimal base_rate
        decimal per_km_rate
        decimal per_hour_rate
        decimal minimum_charge
        string description
        date effective_from
        date effective_to
        timestamp created_at
        timestamp updated_at
    }
    
    header_templates {
        bigint id PK
        string template_name
        string template_type
        text header_text
        text footer_text
        string variables
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }
```

### Technology Stack Comparison

```mermaid
graph LR
    subgraph "AS-IS (1995)"
        L1[Clipper 5.x]
        L2[MS-DOS]
        L3[DBF Files]
        L4[Text Mode UI]
        L5[Dot Matrix]
    end
    
    subgraph "Migration"
        M[🔄 Modernization<br/>Process]
    end
    
    subgraph "TO-BE (2026)"
        R1[Java 21]
        R2[Spring Boot 3.x]
        R3[H2 Database]
        R4[Angular 17+]
        R5[REST API]
        R6[JWT Security]
        R7[PDF Reports]
        R8[Mobile Ready]
    end
    
    L1 -->|Rewrite| M
    L2 -->|Platform| M
    L3 -->|Migrate| M
    L4 -->|Redesign| M
    L5 -->|Modernize| M
    
    M -->|Backend| R1
    M -->|Framework| R2
    M -->|Database| R3
    M -->|Frontend| R4
    M -->|API| R5
    M -->|Security| R6
    M -->|Reports| R7
    M -->|Future| R8
    
    style M fill:#fff3cd
    style L1 fill:#f8d7da
    style L2 fill:#f8d7da
    style L3 fill:#f8d7da
    style R1 fill:#d4edda
    style R2 fill:#d4edda
    style R3 fill:#d4edda
    style R4 fill:#d4edda
```

### Deployment Architecture

```mermaid
graph TB
    subgraph "Development"
        DEV_FE[Angular Dev Server<br/>:4200]
        DEV_BE[Spring Boot<br/>:8080]
        DEV_DB[(H2 Console<br/>:8080/h2-console)]
    end
    
    subgraph "Production"
        LB[Load Balancer<br/>NGINX]
        
        subgraph "Frontend"
            FE1[Angular App<br/>Static Files]
        end
        
        subgraph "Backend Cluster"
            BE1[Spring Boot<br/>Instance 1]
            BE2[Spring Boot<br/>Instance 2]
            BE3[Spring Boot<br/>Instance N]
        end
        
        subgraph "Database"
            DB[(H2 Database<br/>File Mode)]
            BACKUP[(Backup<br/>Storage)]
        end
        
        subgraph "Monitoring"
            PROM[Prometheus<br/>Metrics]
            GRAF[Grafana<br/>Dashboard]
            LOG[Logging<br/>ELK Stack]
        end
    end
    
    subgraph "CI/CD"
        GIT[GitHub<br/>Repository]
        ACTIONS[GitHub Actions<br/>Pipeline]
        DOCKER[Docker<br/>Registry]
    end
    
    DEV_FE -.->|Build| GIT
    DEV_BE -.->|Commit| GIT
    GIT -->|Trigger| ACTIONS
    ACTIONS -->|Test & Build| DOCKER
    DOCKER -->|Deploy| LB
    
    LB -->|Route| FE1
    LB -->|API Calls| BE1
    LB -->|API Calls| BE2
    LB -->|API Calls| BE3
    
    BE1 --> DB
    BE2 --> DB
    BE3 --> DB
    
    DB -.->|Backup| BACKUP
    
    BE1 -.->|Metrics| PROM
    BE2 -.->|Metrics| PROM
    BE3 -.->|Metrics| PROM
    
    PROM --> GRAF
    BE1 -.->|Logs| LOG
    BE2 -.->|Logs| LOG
    BE3 -.->|Logs| LOG
    
    style DEV_FE fill:#e1f5ff
    style DEV_BE fill:#e1f5ff
    style LB fill:#fff3cd
    style DB fill:#d1ecf1
    style PROM fill:#d4edda
    style GRAF fill:#d4edda
```

---

## 📈 Comparison Summary

| Aspect | AS-IS (1995) | TO-BE (2026) |
|--------|--------------|--------------|
| **Platform** | MS-DOS | Web + Mobile |
| **Users** | Single User | Multi-User |
| **Language** | Clipper 5.x | Java 21 + TypeScript |
| **Database** | DBF Files | H2 (SQL) |
| **UI** | Text Mode | Modern Web UI |
| **Security** | None | JWT + RBAC |
| **API** | None | RESTful API |
| **Scalability** | Limited | Horizontal |
| **Backup** | Manual | Automated |
| **Testing** | Manual | Automated (80%+) |
| **Documentation** | Minimal | Swagger + Docs |
| **Mobile** | No | Ready |
| **Reports** | Dot Matrix | PDF |
| **Images** | No | Yes (BLOB) |
| **Deployment** | Manual Copy | CI/CD |
