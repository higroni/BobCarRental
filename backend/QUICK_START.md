# BobCarRental Backend - Quick Start Guide

## 🚀 Quick Installation (5 minutes)

### Step 1: Install Prerequisites

**Option A: Automatic Installation (Windows)**
```bash
# Run as Administrator
cd bobcarrental/backend
.\install-prerequisites.ps1
```

**Option B: Using Chocolatey**
```bash
# Install Chocolatey first (if not installed)
# Run PowerShell as Administrator:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Then install Java and Maven
choco install temurin21 -y
choco install maven -y
```

**Option C: Manual Installation**
1. **Java 21**: Download from https://adoptium.net/temurin/releases/?version=21
2. **Maven**: Download from https://maven.apache.org/download.cgi

### Step 2: Verify Installation
```bash
java -version
# Should show: openjdk version "21.x.x"

mvn -version
# Should show: Apache Maven 3.9.x
```

### Step 3: Build Project
```bash
cd bobcarrental/backend

# Using Maven Wrapper (no Maven installation needed)
mvnw.cmd clean install

# Or using system Maven
mvn clean install
```

### Step 4: Run Application
```bash
# Using Maven Wrapper
mvnw.cmd spring-boot:run

# Or using system Maven
mvn spring-boot:run
```

**Application will start on: http://localhost:8080**

### Step 5: Run Tests
```bash
# Using provided script
run-tests.bat

# Or directly
mvnw.cmd test
```

## 📋 What You Get

### Backend Features
- ✅ **10 Complete Modules**: Client, VehicleType, Booking, TripSheet, Billing, VehicleImage, Account, Address, StandardFare, HeaderTemplate
- ✅ **JWT Authentication**: Secure login with access and refresh tokens
- ✅ **Role-Based Authorization**: USER and ADMIN roles
- ✅ **Legacy Algorithms**: 1995 Clipper fare calculations ported to Java
- ✅ **GST Calculations**: Indian tax system (CGST+SGST vs IGST)
- ✅ **REST API**: 80+ endpoints with full CRUD operations
- ✅ **H2 Database**: In-memory database for development
- ✅ **Exception Handling**: Global error handling with meaningful messages

### Test Suite (85%+ Coverage)
- ✅ **150+ Test Cases**: Unit and integration tests
- ✅ **10 Test Files**: Comprehensive coverage of all modules
- ✅ **3,500+ Lines**: Well-documented test code
- ✅ **Mockito & JUnit 5**: Modern testing framework
- ✅ **Spring Boot Test**: Full integration testing

## 🎯 Quick API Test

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### 3. Use Token
```bash
curl -X GET http://localhost:8080/api/v1/clients \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🗄️ Database Access

### H2 Console
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:bobcarrental`
- **Username**: `sa`
- **Password**: (leave empty)

## 📊 Available Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout
- `GET /api/v1/auth/me` - Get current user

### Clients
- `GET /api/v1/clients` - Get all clients
- `GET /api/v1/clients/{id}` - Get client by ID
- `POST /api/v1/clients` - Create client
- `PUT /api/v1/clients/{id}` - Update client
- `DELETE /api/v1/clients/{id}` - Delete client

### Vehicle Types
- `GET /api/v1/vehicle-types` - Get all vehicle types
- `GET /api/v1/vehicle-types/{id}` - Get vehicle type by ID
- `POST /api/v1/vehicle-types` - Create vehicle type (ADMIN)
- `PUT /api/v1/vehicle-types/{id}` - Update vehicle type (ADMIN)
- `DELETE /api/v1/vehicle-types/{id}` - Delete vehicle type (ADMIN)

### Bookings
- `GET /api/v1/bookings` - Get all bookings
- `GET /api/v1/bookings/{id}` - Get booking by ID
- `POST /api/v1/bookings` - Create booking
- `PUT /api/v1/bookings/{id}` - Update booking
- `PUT /api/v1/bookings/{id}/confirm` - Confirm booking
- `PUT /api/v1/bookings/{id}/cancel` - Cancel booking

### Trip Sheets
- `GET /api/v1/trip-sheets` - Get all trip sheets
- `GET /api/v1/trip-sheets/{id}` - Get trip sheet by ID
- `POST /api/v1/trip-sheets` - Create trip sheet
- `PUT /api/v1/trip-sheets/{id}/close` - Close trip sheet

### Billing
- `GET /api/v1/billings` - Get all billings
- `GET /api/v1/billings/{id}` - Get billing by ID
- `POST /api/v1/billings` - Create billing
- `PUT /api/v1/billings/{id}/pay` - Mark as paid

## 🧪 Running Tests

### All Tests
```bash
mvnw.cmd test
```

### Specific Test Class
```bash
mvnw.cmd test -Dtest=ClientServiceTest
```

### With Coverage Report
```bash
mvnw.cmd clean test jacoco:report
```
Report: `target/site/jacoco/index.html`

### Unit Tests Only
```bash
mvnw.cmd test -Dtest=*ServiceTest
```

### Integration Tests Only
```bash
mvnw.cmd test -Dtest=*IntegrationTest
```

## 🐛 Troubleshooting

### Port 8080 in use
Change port in `application.properties`:
```properties
server.port=8081
```

### Java version error
Ensure Java 21 is default:
```bash
java -version
```

### Maven not found
Use Maven Wrapper instead:
```bash
mvnw.cmd clean install
```

### Tests failing
Check test configuration:
```bash
cat src/main/resources/application-test.properties
```

## 📁 Project Structure
```
bobcarrental/backend/
├── src/main/java/com/bobcarrental/
│   ├── config/          # Security, CORS, etc.
│   ├── controller/      # REST endpoints
│   ├── dto/             # Request/Response objects
│   ├── exception/       # Error handling
│   ├── model/           # JPA entities
│   ├── repository/      # Data access
│   ├── security/        # JWT, UserDetails
│   └── service/         # Business logic
├── src/test/java/       # Tests (150+ cases)
├── pom.xml              # Dependencies
└── QUICK_START.md       # This file
```

## 🎓 Next Steps

1. ✅ Backend running
2. ✅ Tests passing
3. ⏳ Add database migrations
4. ⏳ Create seed data
5. ⏳ Build frontend (Angular)
6. ⏳ Deploy to production

## 📚 Documentation

- **Full Setup Guide**: `SETUP_AND_RUN.md`
- **Test Documentation**: `src/test/java/README_TESTS.md`
- **Architecture**: `../docs/ARCHITECTURE_DIAGRAMS.md`
- **Implementation Plan**: `../docs/IMPLEMENTATION_PLAN.md`

## 💡 Tips

- Use **Postman** or **Insomnia** for API testing
- Check **H2 Console** to view database
- Review **logs/** directory for debugging
- Run tests before committing code
- Use **Maven Wrapper** if Maven not installed

## 🆘 Need Help?

1. Check logs in `logs/` directory
2. Review test results
3. Check `SETUP_AND_RUN.md` for detailed instructions
4. Verify Java 21 and Maven are installed

---

**Ready to go! Start the application and test the API endpoints.**