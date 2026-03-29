#!/usr/bin/env node

/**
 * Module Generator for Bob Car Rental Frontend
 * Generates List, Form, and Details Dialog components for a given entity
 * 
 * Usage: node generate-module.js <EntityName>
 * Example: node generate-module.js VehicleType
 */

const fs = require('fs');
const path = require('path');

// Get entity name from command line
const entityName = process.argv[2];

if (!entityName) {
  console.error('❌ Error: Entity name is required');
  console.log('Usage: node generate-module.js <EntityName>');
  console.log('Example: node generate-module.js VehicleType');
  process.exit(1);
}

// Convert entity name to different cases
const pascalCase = entityName; // VehicleType
const camelCase = entityName.charAt(0).toLowerCase() + entityName.slice(1); // vehicleType
const kebabCase = entityName.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase(); // vehicle-type
const snakeCase = entityName.replace(/([a-z])([A-Z])/g, '$1_$2').toLowerCase(); // vehicle_type

console.log(`\n🚀 Generating module for: ${pascalCase}`);
console.log(`   - Pascal Case: ${pascalCase}`);
console.log(`   - Camel Case: ${camelCase}`);
console.log(`   - Kebab Case: ${kebabCase}`);
console.log(`   - Snake Case: ${snakeCase}\n`);

// Base directory
const baseDir = path.join(__dirname, 'src', 'app', 'features', kebabCase);

// Create directories
const dirs = [
  baseDir,
  path.join(baseDir, `${kebabCase}-list`),
  path.join(baseDir, `${kebabCase}-form`),
  path.join(baseDir, `${kebabCase}-details-dialog`)
];

dirs.forEach(dir => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
    console.log(`✅ Created directory: ${dir}`);
  }
});

// Template for List Component TypeScript
const listTsTemplate = `import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
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
import { ${pascalCase}Service } from '../../../core/services/${kebabCase}.service';
import { ${pascalCase}Response } from '../../../models';
import { ${pascalCase}DetailsDialogComponent } from '../${kebabCase}-details-dialog/${kebabCase}-details-dialog';

@Component({
  selector: 'app-${kebabCase}-list',
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
  templateUrl: './${kebabCase}-list.html',
  styleUrls: ['./${kebabCase}-list.css']
})
export class ${pascalCase}ListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['id', 'name', 'actions']; // TODO: Update columns
  dataSource: MatTableDataSource<${pascalCase}Response>;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private ${camelCase}Service: ${pascalCase}Service,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<${pascalCase}Response>([]);
    
    // Set up custom filter predicate for searching
    this.dataSource.filterPredicate = (data: ${pascalCase}Response, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        // TODO: Add searchable fields
        false
      );
    };
  }

  ngOnInit(): void {
    this.load${pascalCase}s();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  load${pascalCase}s(): void {
    this.isLoading = true;
    this.${camelCase}Service.getAll${pascalCase}s().subscribe({
      next: (items) => {
        this.dataSource.data = items;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading ${kebabCase}s:', error);
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

  view${pascalCase}(item: ${pascalCase}Response): void {
    const dialogRef = this.dialog.open(${pascalCase}DetailsDialogComponent, {
      width: '600px',
      data: item,
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'edit') {
        this.edit${pascalCase}(result.item);
      }
    });
  }

  edit${pascalCase}(item: ${pascalCase}Response): void {
    this.router.navigate(['/${kebabCase}', item.id, 'edit']);
  }

  delete${pascalCase}(item: ${pascalCase}Response): void {
    if (confirm(\`Are you sure you want to delete this ${kebabCase}?\`)) {
      this.${camelCase}Service.delete${pascalCase}(item.id).subscribe({
        next: () => {
          console.log('${pascalCase} deleted successfully');
          this.load${pascalCase}s();
        },
        error: (error) => {
          console.error('Error deleting ${kebabCase}:', error);
          alert('Failed to delete ${kebabCase}. Please try again.');
        }
      });
    }
  }

  create${pascalCase}(): void {
    this.router.navigate(['/${kebabCase}/new']);
  }
}

// Made with Bob
`;

// Write files
const listTsPath = path.join(baseDir, `${kebabCase}-list`, `${kebabCase}-list.ts`);
fs.writeFileSync(listTsPath, listTsTemplate);
console.log(`✅ Created: ${listTsPath}`);

console.log(`\n✨ Module generation complete!`);
console.log(`\n📝 Next steps:`);
console.log(`   1. Update displayedColumns in ${kebabCase}-list.ts`);
console.log(`   2. Update filterPredicate with searchable fields`);
console.log(`   3. Create ${kebabCase}.service.ts in core/services`);
console.log(`   4. Create ${kebabCase}.model.ts in models`);
console.log(`   5. Add routes to app.routes.ts`);
console.log(`   6. Generate HTML and SCSS files`);

// Made with Bob