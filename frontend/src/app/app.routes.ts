import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Default route - redirect to dashboard
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },

  // Login route - accessible only to guests (not authenticated users)
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login),
    canActivate: [guestGuard]
  },

  // Dashboard route - protected, requires authentication
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard),
    canActivate: [authGuard]
  },

  // Clients routes - protected, requires authentication
  {
    path: 'clients',
    loadComponent: () => import('./features/clients/client-list/client-list').then(m => m.ClientListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'clients/new',
    loadComponent: () => import('./features/clients/client-form/client-form').then(m => m.ClientFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'clients/:id/edit',
    loadComponent: () => import('./features/clients/client-form/client-form').then(m => m.ClientFormComponent),
    canActivate: [authGuard]
  },

  // Vehicle Types routes - protected, requires authentication
  {
    path: 'vehicle-types',
    loadComponent: () => import('./features/vehicle-types/vehicle-type-list/vehicle-type-list').then(m => m.VehicleTypeListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'vehicle-types/new',
    loadComponent: () => import('./features/vehicle-types/vehicle-type-form/vehicle-type-form').then(m => m.VehicleTypeFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'vehicle-types/:id/edit',
    loadComponent: () => import('./features/vehicle-types/vehicle-type-form/vehicle-type-form').then(m => m.VehicleTypeFormComponent),
    canActivate: [authGuard]
  },

  // Address routes - protected, requires authentication
  {
    path: 'addresses',
    loadComponent: () => import('./features/addresses/address-list/address-list').then(m => m.AddressListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'addresses/new',
    loadComponent: () => import('./features/addresses/address-form/address-form').then(m => m.AddressFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'addresses/:id/edit',
    loadComponent: () => import('./features/addresses/address-form/address-form').then(m => m.AddressFormComponent),
    canActivate: [authGuard]
  },

  // Account routes - protected, requires authentication
  {
    path: 'accounts',
    loadComponent: () => import('./features/accounts/account-list/account-list').then(m => m.AccountListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'accounts/new',
    loadComponent: () => import('./features/accounts/account-form/account-form').then(m => m.AccountFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'accounts/:id/edit',
    loadComponent: () => import('./features/accounts/account-form/account-form').then(m => m.AccountFormComponent),
    canActivate: [authGuard]
  },

  // Standard Fare routes - protected, requires authentication
  {
    path: 'standard-fares',
    loadComponent: () => import('./features/standard-fares/standard-fare-list/standard-fare-list').then(m => m.StandardFareListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'standard-fares/new',
    loadComponent: () => import('./features/standard-fares/standard-fare-form/standard-fare-form').then(m => m.StandardFareFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'standard-fares/:id/edit',
    loadComponent: () => import('./features/standard-fares/standard-fare-form/standard-fare-form').then(m => m.StandardFareFormComponent),
    canActivate: [authGuard]
  },

  // User routes - protected, requires authentication (ADMIN only)
  {
    path: 'users',
    loadComponent: () => import('./features/users/user-list/user-list').then(m => m.UserListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'users/new',
    loadComponent: () => import('./features/users/user-form/user-form').then(m => m.UserFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'users/:id/edit',
    loadComponent: () => import('./features/users/user-form/user-form').then(m => m.UserFormComponent),
    canActivate: [authGuard]
  },

  // Header Template routes - protected, requires authentication (ADMIN only)
  {
    path: 'header-templates',
    loadComponent: () => import('./features/header-templates/header-template-list/header-template-list').then(m => m.HeaderTemplateListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'header-templates/new',
    loadComponent: () => import('./features/header-templates/header-template-form/header-template-form').then(m => m.HeaderTemplateFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'header-templates/:id/edit',
    loadComponent: () => import('./features/header-templates/header-template-form/header-template-form').then(m => m.HeaderTemplateFormComponent),
    canActivate: [authGuard]
  },

  // Booking routes - protected, requires authentication
  {
    path: 'bookings',
    loadComponent: () => import('./features/bookings/booking-list/booking-list').then(m => m.BookingListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'bookings/new',
    loadComponent: () => import('./features/bookings/booking-form/booking-form').then(m => m.BookingFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'bookings/:id',
    loadComponent: () => import('./features/bookings/booking-form/booking-form').then(m => m.BookingFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'bookings/:id/edit',
    loadComponent: () => import('./features/bookings/booking-form/booking-form').then(m => m.BookingFormComponent),
    canActivate: [authGuard]
  },

  // Trip Sheet routes - protected, requires authentication
  {
    path: 'trip-sheets',
    loadComponent: () => import('./features/trip-sheets/trip-sheet-list/trip-sheet-list').then(m => m.TripSheetListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'trip-sheets/new',
    loadComponent: () => import('./features/trip-sheets/trip-sheet-form/trip-sheet-form').then(m => m.TripSheetFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'trip-sheets/:id',
    loadComponent: () => import('./features/trip-sheets/trip-sheet-form/trip-sheet-form').then(m => m.TripSheetFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'trip-sheets/:id/edit',
    loadComponent: () => import('./features/trip-sheets/trip-sheet-form/trip-sheet-form').then(m => m.TripSheetFormComponent),
    canActivate: [authGuard]
  },

  // Billing routes - protected, requires authentication
  {
    path: 'billings',
    loadComponent: () => import('./features/billings/billing-list/billing-list').then(m => m.BillingListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'billings/new',
    loadComponent: () => import('./features/billings/billing-form/billing-form').then(m => m.BillingFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'billings/:id',
    loadComponent: () => import('./features/billings/billing-form/billing-form').then(m => m.BillingFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'billings/:id/edit',
    loadComponent: () => import('./features/billings/billing-form/billing-form').then(m => m.BillingFormComponent),
    canActivate: [authGuard]
  },

  // Wildcard route - redirect to dashboard
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];

// Made with Bob
