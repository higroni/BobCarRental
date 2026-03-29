import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { ClientService } from '../../core/services/client.service';
import { BookingService } from '../../core/services/booking.service';
import { VehicleTypeService } from '../../core/services/vehicle-type.service';
import { BillingService } from '../../core/services/billing.service';
import { User } from '../../models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  currentUser: User | null = null;
  
  // Stats data
  stats = {
    totalClients: 0,
    activeBookings: 0,
    vehicleTypes: 0,
    pendingInvoices: 0
  };

  modules = [
    { title: 'Clients', icon: 'people', route: '/clients', color: '#4CAF50' },
    { title: 'Vehicle Types', icon: 'directions_car', route: '/vehicle-types', color: '#2196F3' },
    { title: 'Bookings', icon: 'event', route: '/bookings', color: '#FF9800' },
    { title: 'Trip Sheets', icon: 'description', route: '/trip-sheets', color: '#9C27B0' },
    { title: 'Billing', icon: 'receipt', route: '/billings', color: '#F44336' },
    { title: 'Accounts', icon: 'account_balance', route: '/accounts', color: '#00BCD4' },
    { title: 'Address Book', icon: 'contacts', route: '/addresses', color: '#795548' },
    { title: 'Standard Fares', icon: 'attach_money', route: '/standard-fares', color: '#607D8B', adminOnly: true },
    { title: 'Header Templates', icon: 'article', route: '/header-templates', color: '#3F51B5', adminOnly: true },
    { title: 'Users', icon: 'manage_accounts', route: '/users', color: '#E91E63', adminOnly: true }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private clientService: ClientService,
    private bookingService: BookingService,
    private vehicleTypeService: VehicleTypeService,
    private billingService: BillingService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadStats();
  }

  /**
   * Load dashboard statistics
   */
  loadStats(): void {
    forkJoin({
      clients: this.clientService.getAllClients(),
      bookings: this.bookingService.getBookings(),
      vehicleTypes: this.vehicleTypeService.getAllVehicleTypes(),
      billings: this.billingService.getBillings()
    }).subscribe({
      next: (results) => {
        this.stats.totalClients = results.clients.length;
        this.stats.activeBookings = results.bookings.filter(b => b.status !== 'COMPLETED' && b.status !== 'CANCELLED').length;
        this.stats.vehicleTypes = results.vehicleTypes.length;
        this.stats.pendingInvoices = results.billings.filter(b => !b.paid).length;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
      }
    });
  }

  /**
   * Navigate to module
   */
  navigateToModule(route: string): void {
    this.router.navigate([route]);
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * Get visible modules based on user role
   */
  getVisibleModules() {
    if (this.isAdmin()) {
      return this.modules;
    }
    return this.modules.filter(m => !m.adminOnly);
  }
}

// Made with Bob
