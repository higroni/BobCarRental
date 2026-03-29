import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AddressService } from '../../../core/services/address.service';
import { AddressResponse } from '../../../models';
import { AddressDetailsDialogComponent } from '../address-details-dialog/address-details-dialog';

@Component({
  selector: 'app-address-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDialogModule
  ],
  templateUrl: './address-list.html',
  styleUrls: ['./address-list.css']
})
export class AddressListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['clientId', 'name', 'city', 'phone', 'isActive', 'actions'];
  dataSource: MatTableDataSource<AddressResponse>;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private addressService: AddressService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<AddressResponse>([]);
    
    this.dataSource.filterPredicate = (data: AddressResponse, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        data.clientId?.toLowerCase().includes(searchStr) ||
        data.name?.toLowerCase().includes(searchStr) ||
        data.city?.toLowerCase().includes(searchStr) ||
        data.phone?.toLowerCase().includes(searchStr) ||
        data.companyName?.toLowerCase().includes(searchStr) ||
        false
      );
    };
  }

  ngOnInit(): void {
    this.loadAddresses();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadAddresses(): void {
    this.isLoading = true;
    this.addressService.getAllAddresses().subscribe({
      next: (addresses) => {
        this.dataSource.data = addresses;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading addresses:', error);
        this.isLoading = false;
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  viewAddress(address: AddressResponse): void {
    const dialogRef = this.dialog.open(AddressDetailsDialogComponent, {
      width: '600px',
      data: address,
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'edit') {
        this.editAddress(result.address);
      }
    });
  }

  createAddress(): void {
    this.router.navigate(['/addresses/new']);
  }

  editAddress(address: AddressResponse): void {
    this.router.navigate(['/addresses', address.id, 'edit']);
  }

  deleteAddress(address: AddressResponse): void {
    if (confirm(`Are you sure you want to delete address "${address.name}"?`)) {
      this.addressService.deleteAddress(address.id).subscribe({
        next: () => {
          console.log('Address deleted successfully');
          this.loadAddresses();
        },
        error: (error) => {
          console.error('Error deleting address:', error);
        }
      });
    }
  }
}

// Made with Bob