# BobCarRental - Modern Car Rental Management System

A modern web application for car rental fleet management, migrated from a legacy Clipper/Harbour DOS application (Alankar Travels, 1995) to a contemporary tech stack.

## 🚀 Project Overview

**BobCarRental** is a complete rewrite of a 1995 DOS-based taxi booking and fleet management system, now featuring:
- Modern RESTful API backend (Java 21 + Spring Boot)
- Responsive Angular frontend
- Mobile-ready architecture
- Comprehensive security with role-based access control
- Automated testing (backend + E2E)

## 📋 Features

### Core Modules
- ✅ **Client Management** - Customer database with full CRUD operations
- ✅ **Vehicle Type Management** - Fleet management with image uploads
- ✅ **Booking System** - Reservation management with date/time validation
- ✅ **Trip Sheets** - Confirmatory orders and scheduling
- ✅ **Accounts** - Financial management and receipts
- ✅ **Billing** - Invoice generation with fare calculations
- ✅ **Address Book** - Contact management
- ✅ **Standard Fares** - Rate card management (ADMIN only)
- ✅ **Header Templates** - Document template management (ADMIN only)
- ✅ **Reports** - PDF report generation

### Key Features
- 🔐 JWT-based authentication with refresh tokens
- 👥 Role-based access control (ADMIN/USER)
- 📱 Mobile-ready RESTful API
- 🖼️ Image upload with automatic thumbnail generation
- 📊 Pagination and advanced filtering
- 🌍 Internationalization (Serbian/English)
- 📝 Comprehensive validation (migrated from legacy system)
- 🧪 80%+ test coverage
- 📖 Swagger/OpenAPI documentation

## 🏗️ Architecture

```
bobcarrental/
├── backend/          # Java 21 + Spring Boot 3.x
├── frontend/         # Angular 17+
├── migration/        # DBF to H2 migration tools
├── docs/             # Documentation
└── README.md         # This file
```

### Technology Stack

#### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.x** - Application framework
- **Spring Security** - JWT + Role-based access
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database
- **Lombok** - Boilerplate reduction
- **MapStruct** - DTO mapping
- **Liquibase** - Database migrations
- **Swagger/OpenAPI** - API documentation
- **JUnit 5 + Mockito** - Testing

#### Frontend
- **Angular 17+** - Frontend framework
- **Angular Material** - UI components
- **RxJS** - Reactive programming
- **Playwright** - E2E testing

#### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **GitHub Actions** - CI/CD pipeline

## 👥 User Roles

### ADMIN (admin/admin)
- Full access to all modules
- **Exclusive access to:**
  - Standard Fares Management
  - Header Template Management

### USER (user/user)
- Access to all operational modules:
  - Client Management
  - Vehicle Type Management
  - Booking System
  - Trip Sheets
  - Accounts
  - Billing
  - Address Book
  - Reports

## 🚦 Getting Started

### Prerequisites
- Java 21 or higher
- Node.js 18+ and npm
- Docker (optional)
- Git

### Quick Start

#### Backend
```bash
cd bobcarrental/backend
./mvnw spring-boot:run
```
Backend will start on `http://localhost:8080`

#### Frontend
```bash
cd bobcarrental/frontend
npm install
npm start
```
Frontend will start on `http://localhost:4200`

### Docker Deployment
```bash
cd bobcarrental
docker-compose up
```

## 📚 API Documentation

Once the backend is running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### API Endpoints

```
/api/v1/auth          - Authentication (login, logout, refresh)
/api/v1/clients       - Client management
/api/v1/vehicles      - Vehicle type management
/api/v1/vehicles/{id}/images - Vehicle image management
/api/v1/bookings      - Booking management
/api/v1/tripsheets    - Trip sheet management
/api/v1/accounts      - Account management
/api/v1/billing       - Billing operations
/api/v1/addresses     - Address book
/api/v1/fares         - Standard fares (ADMIN only)
/api/v1/templates     - Header templates (ADMIN only)
/api/v1/reports       - Report generation
/api/v1/health        - Health check
```

## 🧪 Testing

### Backend Tests
```bash
cd bobcarrental/backend
./mvnw test
```

### Frontend E2E Tests
```bash
cd bobcarrental/frontend
npm run e2e
```

## 📊 Data Migration

The project includes tools to migrate data from the legacy DBF files:

```bash
cd bobcarrental/migration
# Follow migration guide in docs/MIGRATION_GUIDE.md
```

### Migrated Data
- CLIENT.DBF → Client entities
- VEHTYPE.DBF → VehicleType entities
- BOOKING.DBF → Booking entities
- TRPSHEET.DBF → TripSheet entities
- ACCOUNTS.DBF → Account entities
- ADDRESS.DBF → Address entities
- FARES.TXT → StandardFare entities
- HEADER.TXT → HeaderTemplate entities

## 🔐 Security

### Authentication
- JWT tokens with 15-minute expiry
- Refresh tokens with 7-day expiry
- BCrypt password hashing

### Authorization
- Role-based access control (@PreAuthorize)
- Method-level security
- CORS configuration for web and mobile clients

## 📱 Mobile Integration

The API is designed to support future mobile applications:
- RESTful design with versioning (/api/v1/)
- Thumbnail images for faster loading
- Pagination and filtering
- Standardized response format
- Refresh token support
- Rate limiting ready

See `docs/MOBILE_INTEGRATION_GUIDE.md` for details.

## 📖 Documentation

- [Modernization Plan](../MODERNIZATION_PLAN.md) - Complete modernization strategy
- [API Documentation](docs/API_DOCUMENTATION.md) - Detailed API reference
- [User Manual](docs/USER_MANUAL.md) - End-user guide
- [Technical Documentation](docs/TECHNICAL_DOCUMENTATION.md) - Architecture and design
- [Migration Guide](docs/MIGRATION_GUIDE.md) - Legacy data migration
- [Mobile Integration Guide](docs/MOBILE_INTEGRATION_GUIDE.md) - For mobile developers

## 🤝 Contributing

This is a modernization project. For contributions:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is a modernization of the original Alankar Travels application.
- Original application: © 1995 T.N.C.Venkata Rangan
- Modernized version: [Your License Here]

## 🙏 Acknowledgments

- Original Alankar Travels application by T.N.C.Venkata Rangan (1995)
- Rick Spence's Clipper Programming Guide
- Spring Boot and Angular communities

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Check documentation in `/docs`
- Review API documentation at `/swagger-ui.html`

---

**Version**: 1.0.0  
**Last Updated**: March 26, 2026  
**Status**: In Development