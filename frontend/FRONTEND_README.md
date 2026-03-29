# Bob Car Rental - Angular Frontend

Modern Angular 21 frontend application for Bob Car Rental System.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ (LTS recommended, avoid odd versions like 25.x)
- npm 9+
- Angular CLI 21+

### Installation

```bash
# Install dependencies
npm install

# Start development server
ng serve

# Open browser
http://localhost:4200
```

## 📁 Project Structure

```
src/
├── app/
│   ├── core/              # Core services, guards, interceptors
│   │   ├── services/      # Auth, API, Storage services
│   │   ├── guards/        # Route guards (auth, role)
│   │   └── interceptors/  # HTTP interceptors (JWT, error)
│   ├── shared/            # Shared components, pipes, directives
│   │   ├── components/    # Reusable UI components
│   │   ├── pipes/         # Custom pipes
│   │   └── directives/    # Custom directives
│   ├── features/          # Feature modules
│   │   ├── auth/          # Login, logout
│   │   ├── dashboard/     # Main dashboard
│   │   ├── clients/       # Client management
│   │   ├── vehicles/      # Vehicle type management
│   │   ├── bookings/      # Booking management
│   │   ├── trip-sheets/   # Trip sheet management
│   │   ├── billing/       # Billing/invoicing
│   │   ├── accounts/      # Account management
│   │   ├── addresses/     # Address book
│   │   ├── fares/         # Standard fares (ADMIN only)
│   │   └── templates/     # Header templates (ADMIN only)
│   ├── models/            # TypeScript interfaces
│   └── app.routes.ts      # Application routing
├── environments/          # Environment configurations
│   ├── environment.ts     # Development
│   └── environment.prod.ts # Production
└── styles.scss            # Global styles

```

## 🔧 Development

### Run Development Server
```bash
ng serve
# or with specific port
ng serve --port 4200
```

### Build for Production
```bash
ng build --configuration production
```

### Run Tests
```bash
# Unit tests
ng test

# E2E tests
ng e2e
```

### Code Generation
```bash
# Generate component
ng generate component features/clients/client-list

# Generate service
ng generate service core/services/auth

# Generate guard
ng generate guard core/guards/auth

# Generate interceptor
ng generate interceptor core/interceptors/jwt
```

## 🎨 UI Framework

- **Angular Material** - Material Design components
- **SCSS** - Styling with variables and mixins
- **Responsive Design** - Mobile-first approach

## 🔐 Authentication

- JWT token-based authentication
- Role-based access control (ADMIN, USER)
- Token stored in localStorage
- Auto-refresh on expiry
- Route guards for protected pages

## 📡 API Integration

Backend API: `http://localhost:8080/api/v1`

### API Endpoints
- `/auth/login` - User login
- `/auth/logout` - User logout
- `/auth/refresh` - Refresh token
- `/clients` - Client CRUD
- `/vehicles` - Vehicle type CRUD
- `/bookings` - Booking CRUD
- `/tripsheets` - Trip sheet CRUD
- `/billings` - Billing CRUD
- `/accounts` - Account CRUD
- `/addresses` - Address CRUD
- `/fares` - Standard fares (ADMIN)
- `/templates` - Header templates (ADMIN)

## 🧪 Testing Strategy

### Unit Tests
- Component tests with TestBed
- Service tests with HttpClientTestingModule
- Guard and interceptor tests

### E2E Tests
- Playwright for end-to-end testing
- User flow testing
- Cross-browser testing

## 📦 Dependencies

### Core
- `@angular/core` - Angular framework
- `@angular/router` - Routing
- `@angular/forms` - Reactive forms
- `@angular/common/http` - HTTP client

### UI
- `@angular/material` - Material Design
- `@angular/cdk` - Component Dev Kit

### Utilities
- `rxjs` - Reactive programming
- `date-fns` - Date manipulation

## 🚀 Deployment

### Development
```bash
ng serve
```

### Production Build
```bash
ng build --configuration production
# Output: dist/frontend/
```

### Docker
```bash
# Build image
docker build -t bobcarrental-frontend .

# Run container
docker run -p 80:80 bobcarrental-frontend
```

## 🔄 State Management

- Services with RxJS BehaviorSubject
- Local state in components
- Shared state in services

## 🎯 Features

### Implemented
- ✅ Project structure
- ✅ Angular Material setup
- ✅ Environment configuration
- ✅ Folder organization

### To Implement
- ⏳ Authentication module
- ⏳ Dashboard
- ⏳ CRUD modules (10 modules)
- ⏳ Shared components
- ⏳ HTTP interceptors
- ⏳ Route guards
- ⏳ Error handling
- ⏳ Loading indicators
- ⏳ Notifications/Toasts

## 📝 Coding Standards

- Use TypeScript strict mode
- Follow Angular style guide
- Use reactive forms
- Implement OnPush change detection
- Use standalone components
- Lazy load feature modules
- Use async pipe for observables

## 🐛 Troubleshooting

### Port Already in Use
```bash
ng serve --port 4201
```

### Node Version Warning
Use Node.js LTS version (18.x or 20.x) instead of odd versions (25.x)

### Build Errors
```bash
# Clear cache
rm -rf node_modules package-lock.json
npm install
```

## 📚 Resources

- [Angular Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

## 👥 Team

- **Backend**: Java 21 + Spring Boot 3.x
- **Frontend**: Angular 21 + Material Design
- **Database**: H2 (in-memory)
- **Authentication**: JWT

## 📄 License

© 2026 Bob Car Rental System