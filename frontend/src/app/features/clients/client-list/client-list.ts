import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { ClientService } from '../../../core/services/client.service';
import { ClientResponse } from '../../../models';
import { ClientDetailsDialogComponent } from '../client-details-dialog/client-details-dialog';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatCardModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './client-list.html',
  styleUrls: ['./client-list.css']
})
export class ClientListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['clientId', 'clientName', 'city', 'phone', 'place', 'actions'];
  dataSource: MatTableDataSource<ClientResponse>;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private clientService: ClientService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<ClientResponse>([]);
    
    // Set up custom filter predicate for searching
    this.dataSource.filterPredicate = (data: ClientResponse, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        data.clientId?.toLowerCase().includes(searchStr) ||
        data.clientName?.toLowerCase().includes(searchStr) ||
        data.city?.toLowerCase().includes(searchStr) ||
        data.phone?.toLowerCase().includes(searchStr) ||
        data.place?.toLowerCase().includes(searchStr) ||
        false
      );
    };
  }

  ngOnInit(): void {
    this.loadClients();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadClients(): void {
    this.isLoading = true;
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.dataSource.data = clients;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
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

  viewClient(client: ClientResponse): void {
    const dialogRef = this.dialog.open(ClientDetailsDialogComponent, {
      width: '600px',
      data: client,
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'edit') {
        this.editClient(result.client);
      }
    });
  }

  editClient(client: ClientResponse): void {
    this.router.navigate(['/clients', client.id, 'edit']);
  }

  deleteClient(client: ClientResponse): void {
    if (confirm(`Are you sure you want to delete client "${client.clientName}"?`)) {
      this.clientService.deleteClient(client.id).subscribe({
        next: () => {
          console.log('Client deleted successfully');
          this.loadClients();
        },
        error: (error) => {
          console.error('Error deleting client:', error);
          alert('Failed to delete client. Please try again.');
        }
      });
    }
  }

  createClient(): void {
    this.router.navigate(['/clients/new']);
  }
}

// Made with Bob
