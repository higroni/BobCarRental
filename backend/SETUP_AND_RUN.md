# BobCarRental Backend - Setup and Run Guide

## Prerequisites

### Required Software
1. **Java Development Kit (JDK) 21**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Maven 3.9+** (Optional - Maven Wrapper included)
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **Git** (for version control)
   - Download: https://git-scm.com/

### Optional Tools
- **IntelliJ IDEA** or **Eclipse** (IDE)
- **Postman** or **Insomnia** (API testing)
- **DBeaver** or **H2 Console** (Database viewer)

## Installation Steps

### 1. Install Java 21
```bash
# Windows (using Chocolatey)
choco install temurin21

# Or download installer from:
# https://adoptium.net/temurin/releases/?version=21
```

### 2. Install Maven (Optional)
Maven Wrapper is included in the project, but you can install Maven globally:

```bash
# Windows (using Chocolatey)
choco install maven

# Or download from:
# https://maven.apache.org/download.cgi
```

### 3. Verify Installation
```bash
java -version
# Should show: openjdk version "21.x.x"

mvn -version
# Should show: Apache Maven 3.9.x
```

## Project Setup

### 1. Navigate to Backend Directory
```bash
cd bobcarrental/backend
```

### 2. Install Dependencies
Using Maven Wrapper (recommended):
```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

Or using system Maven:
```bash
mvn clean install
```

### 3. Configure Application
Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration (H2 for development)
spring.datasource.url=jdbc:h2:mem:bobcarrental
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# JWT Configuration
jwt.secret=your-secret-key-change-in-production
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Logging
logging.level.com.bobcarrental=DEBUG
```

## Running the Application

### Method 1: Using Maven Wrapper (Recommended)
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Method 2: Using System Maven
```bash
mvn spring-boot:run
```

### Method 3: Using JAR File
```bash
# Build JAR
mvnw.cmd clean package

# Run JAR
java -jar target/bobcarrental-0.0.1-SNAPSHOT.jar
```

### Method 4: Using IDE
1. Open project in IntelliJ IDEA or Eclipse
2. Find `BobCarRentalApplication.java`
3. Right-click → Run

## Running Tests

### Run All Tests
```bash
# Windows
mvnw.cmd test

# Or use the provided script
run-tests.bat
```

### Run Specific Test Class
```bash
mvnw.cmd test -Dtest=ClientServiceTest
```

### Run with Coverage Report
```bash
mvnw.cmd clean test jacoco:report
```

Coverage report will be in: `target/site/jacoco/index.html`

## Accessing the Application

### API Endpoints
- **Base URL**: http://localhost:8080
- **API Base**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/actuator/health

### H2 Database Console
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:bobcarrental
- **Username**: sa
- **Password**: (leave empty)

### Swagger UI (if configured)
- **URL**: http://localhost:8080/swagger-ui.html

## API Testing

### 1. Register a User
```bash
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "fullName": "Test User"
}
```

### 2. Login
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Response will include JWT token:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 3. Use Token in Requests
```bash
GET http://localhost:8080/api/v1/clients
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Common Issues and Solutions

### Issue 1: Port 8080 Already in Use
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue 2: Java Version Mismatch
**Error**: `Unsupported class file major version`
**Solution**: Ensure Java 21 is installed and set as default:
```bash
java -version
# Should show version 21.x.x
```

### Issue 3: Maven Build Fails
**Solution**: Clean and rebuild:
```bash
mvnw.cmd clean install -U
```

### Issue 4: Tests Fail
**Solution**: Check test configuration in `application-test.properties`

### Issue 5: Database Connection Error
**Solution**: H2 is in-memory, no setup needed. Check if application.properties is correct.

## Development Workflow

### 1. Make Code Changes
Edit files in `src/main/java/com/bobcarrental/`

### 2. Run Tests
```bash
mvnw.cmd test
```

### 3. Run Application
```bash
mvnw.cmd spring-boot:run
```

### 4. Test API
Use Postman or curl to test endpoints

### 5. Check Logs
Logs appear in console and `logs/` directory

## Production Deployment

### 1. Build Production JAR
```bash
mvnw.cmd clean package -DskipTests
```

### 2. Configure Production Properties
Create `application-prod.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/bobcarrental
spring.datasource.username=dbuser
spring.datasource.password=dbpassword
spring.jpa.hibernate.ddl-auto=validate
jwt.secret=production-secret-key-very-long-and-secure
```

### 3. Run with Production Profile
```bash
java -jar target/bobcarrental-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Monitoring and Maintenance

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Application Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### View Logs
```bash
tail -f logs/application.log
```

## Project Structure
```
bobcarrental/backend/
├── src/
│   ├── main/
│   │   ├── java/com/bobcarrental/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security configuration
│   │   │   ├── service/         # Business logic
│   │   │   └── BobCarRentalApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
│       └── java/com/bobcarrental/
│           ├── controller/      # Integration tests
│           ├── service/         # Unit tests
│           └── README_TESTS.md
├── pom.xml                      # Maven configuration
├── run-tests.bat               # Test runner script
└── SETUP_AND_RUN.md           # This file
```

## Next Steps

1. ✅ Backend is running
2. ✅ Tests are passing
3. ⏳ Set up database migrations (Liquibase)
4. ⏳ Create seed data
5. ⏳ Implement frontend (Angular)
6. ⏳ Deploy to production

## Support

For issues or questions:
1. Check logs in `logs/` directory
2. Review test results
3. Check API documentation
4. Review code comments

## Additional Resources

- **Spring Boot Documentation**: https://spring.boot.io/
- **Spring Security**: https://spring.io/projects/spring-security
- **JWT**: https://jwt.io/
- **H2 Database**: https://www.h2database.com/
- **Maven**: https://maven.apache.org/

---

**Note**: This is a development setup. For production deployment, additional security measures, database configuration, and monitoring should be implemented.