import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { BillingService } from '../../../core/services/billing.service';
import { ClientService } from '../../../core/services/client.service';
import { BillingRequest, ClientResponse } from '../../../models';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-billing-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatDividerModule,
    MatListModule,
    MatCheckboxModule
  ],
  templateUrl: './billing-form.html',
  styleUrls: ['./billing-form.css']
})
export class BillingFormComponent implements OnInit {
  billingForm: FormGroup;
  isEditMode = false;
  isViewMode = false;
  billingId: number | null = null;
  loading = false;
  error: string | null = null;
  
  clients: ClientResponse[] = [];
  unbilledTripSheets: any[] = [];
  selectedTripSheets: number[] = [];
  
  paymentModes = [
    'Cash',
    'Credit Card',
    'Debit Card',
    'Bank Transfer',
    'Check',
    'Online Payment'
  ];

  constructor(
    private fb: FormBuilder,
    private billingService: BillingService,
    private clientService: ClientService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.billingForm = this.fb.group({
      billNum: ['', [Validators.required, Validators.min(1)]],
      billDate: [new Date(), Validators.required],
      clientId: ['', Validators.required],
      billAmount: [0, [Validators.required, Validators.min(0)]],
      trpNum: [''],
      printed: [false],
      cancelled: [false],
      billImg: [''],
      tagged: [false]
    });
  }

  ngOnInit(): void {
    this.loadClients();
    this.loadUnbilledTripSheets();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.billingId = +id;
      const url = this.router.url;
      this.isViewMode = !url.includes('/edit');
      this.isEditMode = url.includes('/edit');
      
      this.loadBilling(this.billingId);
      
      if (this.isViewMode) {
        this.billingForm.disable();
      }
    }

  }

  onClientChange(clientId: string): void {
    if (!clientId || this.isEditMode || this.isViewMode) {
      return;
    }

    // Generate bill preview when client is selected
    this.billingService.generateBillPreview(clientId).subscribe({
      next: (preview) => {
        this.billingForm.patchValue({ billImg: preview });
      },
      error: (error) => {
        console.error('Error generating bill preview:', error);
        this.billingForm.patchValue({ billImg: '' });
      }
    });
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.clients = clients;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
      }
    });
  }

  loadUnbilledTripSheets(): void {
    this.billingService.getUnbilledTripSheets().subscribe({
      next: (tripSheets) => {
        this.unbilledTripSheets = tripSheets;
      },
      error: (error) => {
        console.error('Error loading unbilled trip sheets:', error);
      }
    });
  }

  loadBilling(id: number): void {
    this.loading = true;
    this.billingService.getBillingById(id).subscribe({
      next: (billing) => {
        this.billingForm.patchValue({
          billNum: billing.billNo,
          billDate: new Date(billing.billDate),
          clientId: billing.clientId,
          billAmount: billing.totalAmount,
          trpNum: undefined, // Not in response
          printed: undefined, // Not in response
          cancelled: undefined, // Not in response
          billImg: billing.billImg || '', // Load generated bill content
          tagged: billing.tagged
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading billing:', error);
        this.error = 'Failed to load billing. Please try again.';
        this.loading = false;
      }
    });
  }

  calculateTotalFromTripSheets(): void {
    const total = this.unbilledTripSheets
      .filter(ts => this.selectedTripSheets.includes(ts.id))
      .reduce((sum, ts) => sum + (ts.totalAmount || 0), 0);
    
    this.billingForm.patchValue({ billAmount: total });
  }

  toggleTripSheet(tripSheetId: number): void {
    const index = this.selectedTripSheets.indexOf(tripSheetId);
    if (index > -1) {
      this.selectedTripSheets.splice(index, 1);
    } else {
      this.selectedTripSheets.push(tripSheetId);
    }
    this.calculateTotalFromTripSheets();
  }

  isTripSheetSelected(tripSheetId: number): boolean {
    return this.selectedTripSheets.includes(tripSheetId);
  }

  onSubmit(): void {
    if (this.billingForm.invalid) {
      this.billingForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const formValue = this.billingForm.value;
    const request: BillingRequest = {
      billNum: formValue.billNum,
      billDate: this.formatDate(formValue.billDate),
      clientId: formValue.clientId,
      billAmount: formValue.billAmount,
      trpNum: formValue.trpNum || undefined,
      printed: formValue.printed || false,
      cancelled: formValue.cancelled || false,
      billImg: formValue.billImg || undefined,
      tagged: formValue.tagged || false
    };

    const operation = this.isEditMode
      ? this.billingService.updateBilling(this.billingId!, request)
      : this.billingService.createBilling(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/billings']);
      },
      error: (error) => {
        console.error('Error saving billing:', error);
        const errorMessage = error.error?.message || 'Failed to save billing. Please try again.';
        
        // Provide helpful message for common validation errors
        if (errorMessage.includes('non-finished trip sheet')) {
          this.error = 'Cannot create billing for trip sheet that is not finished. Either leave Trip Number empty or finish the trip sheet first.';
        } else if (errorMessage.includes('Bill number already exists')) {
          this.error = 'Bill number already exists. Please use a different number.';
        } else {
          this.error = errorMessage;
        }
        
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/billings']);
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  generatePDF(): void {
    const billContent = this.billingForm.get('billImg')?.value;
    
    if (!billContent) {
      alert('No bill content to generate PDF');
      return;
    }

    // Create new PDF document
    const doc = new jsPDF();
    
    // Set font to monospace for better formatting
    doc.setFont('courier');
    doc.setFontSize(10);
    
    // Split content into lines
    const lines = billContent.split('\n');
    
    // Add content to PDF with proper line spacing
    let yPosition = 10;
    const lineHeight = 5;
    const pageHeight = doc.internal.pageSize.height;
    const margin = 10;
    
    lines.forEach((line: string) => {
      // Check if we need a new page
      if (yPosition > pageHeight - margin) {
        doc.addPage();
        yPosition = margin;
      }
      
      doc.text(line, margin, yPosition);
      yPosition += lineHeight;
    });
    
    // Generate filename
    const billNum = this.billingForm.get('billNum')?.value || 'preview';
    const filename = `bill-${billNum}.pdf`;
    
    // Open in new tab and provide download option
    const pdfBlob = doc.output('blob');
    const pdfUrl = URL.createObjectURL(pdfBlob);
    
    // Open in new tab
    window.open(pdfUrl, '_blank');
    
    // Also trigger download
    const link = document.createElement('a');
    link.href = pdfUrl;
    link.download = filename;
    link.click();
    
    // Clean up
    setTimeout(() => URL.revokeObjectURL(pdfUrl), 100);
  }
}

// Made with Bob