# Bob Car Rental System - Backend

## Overview
Modern car rental management system backend built with Spring Boot 3.x, migrated from legacy Clipper/Harbour DOS application (Alankar Travels, 1995).

## Technology Stack
- **Java**: 21
- **Spring Boot**: 3.2.0
- **Spring Security**: JWT-based authentication
- **Database**: H2 (development), PostgreSQL/MySQL (production)
- **ORM**: JPA/Hibernate
- **DTO Mapping**: MapStruct
- **Database Migration**: Liquibase
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI (planned)

## Project Structure
```
backend/
├── src/main/java/com/bobcarrental/
│   ├── model/              # JPA entities (14 entities)
│   ├── repository/         # Spring Data JPA repositories (12 repos)
│   ├── dto/                # Data Transfer Objects
│   │   ├── common/         # ApiResponse, PageResponse, ErrorDetails
│   │   ├── auth/           # LoginRequest, AuthResponse
│   │   ├── client/         # Client DTOs
│   │   ├── vehicletype/    # VehicleType DTOs
│   │   ├── booking/        # Booking DTOs
│   │   ├── tripsheet/      # TripSheet DTOs
│   │   └── billing/        # Billing DTOs
│   ├── service/            # Business logic layer
│   │   └── impl/           # Service implementations
│   ├── controller/         # REST API controllers
│   ├── security/           # JWT, UserDetails, SecurityConfig
│   └── exception/          # Custom exceptions and handlers
├── src/main/resources/
│   ├── application.properties
│   └── db/changelog/       # Liquibase migrations
└── pom.xml
```

## Features Implemented

### Core Modules (5/10 Complete)
1. ✅ **Client Management** - CRUD operations for clients
2. ✅ **Vehicle Type Management** - Vehicle types with images
3. ✅ **Booking Management** - Reservations with availability checking
4. ✅ **Trip Sheet Management** - Trip tracking with fare calculation
5. ✅ **Billing Management** - Invoicing with GST calculation

### Key Features
- **JWT Authentication** - Stateless authentication with access/refresh tokens
- **Role-Based Authorization** - ADMIN and USER roles
- **Fare Calculation** - Legacy algorithms ported:
  - Local Fare (Flat rate)
  - Outstation Fare (Minimum km + night halt)
  - Split Fare (Hire + Fuel)
- **GST Calculation** - CGST+SGST (intra-state) or IGST (inter-state)
- **Pagination** - All list endpoints support pagination
- **Global Exception Handling** - Consistent error responses
- **Bean Validation** - Comprehensive input validation

## Prerequisites
- Java 21 or higher
- Maven 3.8+
- Git

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bobcarrental/backend
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Configuration

### Database Configuration
Default configuration uses H2 in-memory database. To use PostgreSQL or MySQL:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/bobcarrental
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/bobcarrental
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### JWT Configuration
Update `application.properties`:
```properties
app.jwt.secret=YourSecretKeyMinimum512Bits
app.jwt.expiration-ms=86400000        # 24 hours
app.jwt.refresh-expiration-ms=604800000  # 7 days
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout

### Clients
- `GET /api/v1/clients` - Get all clients (paginated)
- `GET /api/v1/clients/{id}` - Get client by ID
- `POST /api/v1/clients` - Create client (ADMIN)
- `PUT /api/v1/clients/{id}` - Update client (ADMIN)
- `DELETE /api/v1/clients/{id}` - Delete client (ADMIN)

### Vehicle Types
- `GET /api/v1/vehicletypes` - Get all vehicle types
- `GET /api/v1/vehicletypes/{id}` - Get vehicle type by ID
- `POST /api/v1/vehicletypes` - Create vehicle type (ADMIN)
- `PUT /api/v1/vehicletypes/{id}` - Update vehicle type (ADMIN)
- `DELETE /api/v1/vehicletypes/{id}` - Delete vehicle type (ADMIN)

### Bookings
- `GET /api/v1/bookings` - Get all bookings
- `GET /api/v1/bookings/{id}` - Get booking by ID
- `POST /api/v1/bookings` - Create booking
- `PUT /api/v1/bookings/{id}` - Update booking
- `PATCH /api/v1/bookings/{id}/confirm` - Confirm booking (ADMIN)
- `PATCH /api/v1/bookings/{id}/cancel` - Cancel booking
- `GET /api/v1/bookings/check-availability` - Check vehicle availability

### Trip Sheets
- `GET /api/v1/tripsheets` - Get all trip sheets
- `GET /api/v1/tripsheets/{id}` - Get trip sheet by ID
- `POST /api/v1/tripsheets` - Create trip sheet
- `PUT /api/v1/tripsheets/{id}` - Update trip sheet
- `PATCH /api/v1/tripsheets/{id}/start` - Start trip
- `PATCH /api/v1/tripsheets/{id}/finish` - Finish trip (calculates fare)
- `POST /api/v1/tripsheets/{id}/calculate-fare` - Recalculate fare

### Billings
- `GET /api/v1/billings` - Get all billings
- `GET /api/v1/billings/{id}` - Get billing by ID
- `POST /api/v1/billings` - Create billing (ADMIN)
- `PUT /api/v1/billings/{id}` - Update billing (ADMIN)
- `PATCH /api/v1/billings/{id}/payment` - Record payment (ADMIN)
- `GET /api/v1/billings/unpaid` - Get unpaid billings (ADMIN)
- `GET /api/v1/billings/calculate-gst` - Calculate GST

## Default Credentials
```
Username: admin
Password: admin123
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn test jacoco:report
# View report at target/site/jacoco/index.html
```

## H2 Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/bobcarrental`
- Username: `sa`
- Password: (leave empty)

## Legacy Algorithm Migration

### Fare Calculation Algorithms
The system successfully ports three legacy fare calculation algorithms from the 1995 Clipper/Harbour application:

1. **Local Fare (LocFare)**
   - Base hours with base rate
   - Extra kilometer charges
   - Free kilometers included

2. **Outstation Fare (OutFare)**
   - Minimum kilometers per day
   - Extra kilometer charges
   - Night halt charges

3. **Split Fare**
   - Basic hiring charge
   - Fuel cost for normal kilometers
   - Extra kilometer charges

## Development

### Adding a New Module
1. Create entity in `model/`
2. Create repository in `repository/`
3. Create DTOs in `dto/<module>/`
4. Create service interface and implementation
5. Create controller
6. Add Liquibase migration

### Code Style
- Follow Spring Boot best practices
- Use Lombok for boilerplate code
- Use MapStruct for DTO mapping
- Add comprehensive validation
- Include logging
- Write unit and integration tests

## Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Database Connection Issues
```bash
# Check H2 database file
ls -la data/bobcarrental.mv.db

# Delete and restart to recreate
rm -rf data/
mvn spring-boot:run
```

### JWT Token Issues
```bash
# Ensure secret key is at least 512 bits (64 characters)
# Update app.jwt.secret in application.properties
```

## Production Deployment

### Build for Production
```bash
mvn clean package -DskipTests
```

### Run JAR
```bash
java -jar target/bobcarrental-backend-1.0.0.jar
```

### Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://localhost:5432/bobcarrental
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_production_secret_key
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
Proprietary - Bob Car Rental System

## Support
For issues and questions, contact: support@bobcarrental.com

## Changelog

### Version 1.0.0 (Current)
- ✅ Core infrastructure (models, repositories, security)
- ✅ 5 complete modules (Client, VehicleType, Booking, TripSheet, Billing)
- ✅ JWT authentication
- ✅ Legacy fare calculation algorithms
- ✅ GST calculation
- ✅ Pagination support
- ✅ Global exception handling
- ⏳ Remaining 5 modules (VehicleImage, Account, Address, StandardFare, HeaderTemplate)
- ⏳ Complete test suite
- ⏳ API documentation (Swagger)
- ⏳ Docker support

## Architecture Highlights

### Layered Architecture
```
Controller → Service → Repository → Database
     ↓          ↓
   DTO      Business Logic
```

### Security Flow
```
Request → JWT Filter → Authentication → Authorization → Controller
```

### Fare Calculation Flow
```
TripSheet.finish() → FareCalculationService → 
  → Determine fare type (LOCAL/OUTSTATION/SPLIT) →
  → Calculate using legacy algorithms →
  → Update TripSheet with amounts
```

## Performance Considerations
- Use pagination for large datasets
- Implement caching for frequently accessed data (planned)
- Optimize database queries with proper indexing
- Use connection pooling for database connections

## Security Best Practices
- Never commit sensitive data (JWT secrets, passwords)
- Use environment variables for production secrets
- Implement rate limiting (planned)
- Regular security audits
- Keep dependencies updated

---

**Status**: Backend 62.5% Complete (100/160 files)
**Last Updated**: 2026-03-26