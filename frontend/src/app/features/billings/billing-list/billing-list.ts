import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { BillingService } from '../../../core/services/billing.service';
import { BillingSummaryResponse } from '../../../models';

@Component({
  selector: 'app-billing-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule
  ],
  templateUrl: './billing-list.html',
  styleUrls: ['./billing-list.css']
})
export class BillingListComponent implements OnInit {
  billings: BillingSummaryResponse[] = [];
  filteredBillings: BillingSummaryResponse[] = [];
  displayedColumns: string[] = [
    'billNum',
    'billDate',
    'clientId',
    'totalAmount',
    'paid',
    'balance',
    'tagged',
    'status',
    'actions'
  ];
  loading = false;
  error: string | null = null;
  searchTerm = '';

  constructor(
    private billingService: BillingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBillings();
  }

  loadBillings(): void {
    this.loading = true;
    this.error = null;

    this.billingService.getBillings().subscribe({
      next: (billings) => {
        this.billings = billings;
        this.filteredBillings = billings;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading billings:', error);
        this.error = 'Failed to load billings. Please try again.';
        this.loading = false;
      }
    });
  }

  onSearchChange(value: string): void {
    this.searchTerm = value;
    this.applyFilter();
  }

  applyFilter(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.filteredBillings = this.billings;
      return;
    }

    const searchLower = this.searchTerm.toLowerCase().trim();
    this.filteredBillings = this.billings.filter(billing => {
      return (
        (billing.billNum && billing.billNum.toString().includes(searchLower)) ||
        (billing.clientId && billing.clientId.toLowerCase().includes(searchLower)) ||
        (billing.billDate && billing.billDate.toString().includes(searchLower)) ||
        (billing.totalAmount && billing.totalAmount.toString().includes(searchLower))
      );
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilter();
  }

  viewBilling(id: number): void {
    this.router.navigate(['/billings', id]);
  }

  editBilling(id: number): void {
    this.router.navigate(['/billings', id, 'edit']);
  }

  createBilling(): void {
    this.router.navigate(['/billings/new']);
  }

  deleteBilling(id: number, billNum: number): void {
    if (confirm(`Are you sure you want to delete billing #${billNum}?`)) {
      this.billingService.deleteBilling(id).subscribe({
        next: () => {
          this.loadBillings();
        },
        error: (error) => {
          console.error('Error deleting billing:', error);
          alert('Failed to delete billing. Please try again.');
        }
      });
    }
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatDate(date: string | undefined): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getStatusClass(billing: BillingSummaryResponse): string {
    const balance = (billing.totalAmount || 0) - (billing.paid || 0);
    return balance <= 0 ? 'status-paid' : 'status-unpaid';
  }

  getStatusLabel(billing: BillingSummaryResponse): string {
    const balance = (billing.totalAmount || 0) - (billing.paid || 0);
    return balance <= 0 ? 'Paid' : 'Unpaid';
  }

  getBalance(billing: BillingSummaryResponse): number {
    return (billing.totalAmount || 0) - (billing.paid || 0);
  }
}

// Made with Bob