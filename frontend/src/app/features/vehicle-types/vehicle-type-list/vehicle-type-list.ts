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
import { VehicleTypeService } from '../../../core/services/vehicle-type.service';
import { VehicleTypeResponse } from '../../../models/vehicle.model';
import { VehicleTypeDetailsDialogComponent } from '../vehicle-type-details-dialog/vehicle-type-details-dialog';

@Component({
  selector: 'app-vehicle-type-list',
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
  templateUrl: './vehicle-type-list.html',
  styleUrls: ['./vehicle-type-list.css']
})
export class VehicleTypeListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['typeId', 'typeName', 'description', 'isActive', 'actions'];
  dataSource: MatTableDataSource<VehicleTypeResponse>;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private vehicleTypeService: VehicleTypeService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<VehicleTypeResponse>([]);
    
    this.dataSource.filterPredicate = (data: VehicleTypeResponse, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        data.typeId?.toLowerCase().includes(searchStr) ||
        data.typeName?.toLowerCase().includes(searchStr) ||
        data.description?.toLowerCase().includes(searchStr) ||
        false
      );
    };
  }

  ngOnInit(): void {
    this.loadVehicleTypes();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadVehicleTypes(): void {
    this.isLoading = true;
    this.vehicleTypeService.getAllVehicleTypes().subscribe({
      next: (types) => {
        this.dataSource.data = types;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading vehicle types:', error);
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

  viewVehicleType(type: VehicleTypeResponse): void {
    const dialogRef = this.dialog.open(VehicleTypeDetailsDialogComponent, {
      width: '500px',
      data: type,
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'edit') {
        this.editVehicleType(result.type);
      }
    });
  }

  editVehicleType(type: VehicleTypeResponse): void {
    this.router.navigate(['/vehicle-types', type.id, 'edit']);
  }

  deleteVehicleType(type: VehicleTypeResponse): void {
    if (confirm(`Are you sure you want to delete vehicle type "${type.typeName}"?`)) {
      this.vehicleTypeService.deleteVehicleType(type.id).subscribe({
        next: () => {
          console.log('Vehicle type deleted successfully');
          this.loadVehicleTypes();
        },
        error: (error) => {
          console.error('Error deleting vehicle type:', error);
          alert('Failed to delete vehicle type. Please try again.');
        }
      });
    }
  }

  createVehicleType(): void {
    this.router.navigate(['/vehicle-types/new']);
  }
}

// Made with Bob