import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { AccountResponse } from '../../../models';
import { AccountDetailsDialogComponent } from '../account-details-dialog/account-details-dialog.component';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatChipsModule,
    MatDialogModule,
    FormsModule
  ],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.css'
})
export class AccountListComponent implements OnInit {
  accounts: AccountResponse[] = [];
  filteredAccounts: AccountResponse[] = [];
  displayedColumns: string[] = ['num', 'desc', 'clientId', 'date', 'recd', 'bill', 'tagged', 'actions'];
  searchTerm: string = '';
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private accountService: AccountService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.error = null;
    
    this.accountService.getAllAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.filteredAccounts = accounts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.error = 'Failed to load accounts. Please try again.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();
    
    if (!term) {
      this.filteredAccounts = this.accounts;
      return;
    }

    this.filteredAccounts = this.accounts.filter(account =>
      account.num?.toString().includes(term) ||
      account.desc?.toLowerCase().includes(term) ||
      account.clientId?.toLowerCase().includes(term)
    );
  }

  viewDetails(account: AccountResponse): void {
    this.dialog.open(AccountDetailsDialogComponent, {
      width: '600px',
      data: account
    });
  }

  editAccount(id: number): void {
    this.router.navigate(['/accounts', id, 'edit']);
  }

  deleteAccount(id: number, desc: string): void {
    if (confirm(`Are you sure you want to delete account "${desc}"?`)) {
      this.accountService.deleteAccount(id).subscribe({
        next: () => {
          this.loadAccounts();
        },
        error: (error) => {
          console.error('Error deleting account:', error);
          alert('Failed to delete account. Please try again.');
        }
      });
    }
  }

  createAccount(): void {
    this.router.navigate(['/accounts/new']);
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return '0.00';
    return value.toFixed(2);
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}

// Made with Bob