import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { HeaderTemplateService } from '../../../core/services/header-template.service';
import { HeaderTemplateRequest, TemplateType, TEMPLATE_TYPE_LABELS } from '../../../models';

@Component({
  selector: 'app-header-template-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule
  ],
  templateUrl: './header-template-form.html',
  styleUrls: ['./header-template-form.css']
})
export class HeaderTemplateFormComponent implements OnInit {
  templateForm: FormGroup;
  isEditMode = false;
  templateId: number | null = null;
  loading = false;
  error: string | null = null;
  
  templateTypes = Object.values(TemplateType);
  templateTypeLabels = TEMPLATE_TYPE_LABELS;

  constructor(
    private fb: FormBuilder,
    private headerTemplateService: HeaderTemplateService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.templateForm = this.fb.group({
      templateName: ['', [Validators.required, Validators.maxLength(50)]],
      templateContent: ['', Validators.required],
      description: ['', Validators.maxLength(200)],
      templateType: [TemplateType.INVOICE, Validators.required],
      active: [true],
      isDefault: [false]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.templateId = +id;
      this.loadTemplate(this.templateId);
    }
  }

  loadTemplate(id: number): void {
    this.loading = true;
    this.headerTemplateService.getHeaderTemplateById(id).subscribe({
      next: (template) => {
        this.templateForm.patchValue({
          templateName: template.templateName,
          templateContent: template.templateContent,
          description: template.description,
          templateType: template.templateType,
          active: template.active,
          isDefault: template.isDefault
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading template:', error);
        this.error = 'Failed to load template. Please try again.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.templateForm.invalid) {
      this.templateForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const request: HeaderTemplateRequest = this.templateForm.value;

    const operation = this.isEditMode && this.templateId
      ? this.headerTemplateService.updateHeaderTemplate(this.templateId, request)
      : this.headerTemplateService.createHeaderTemplate(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/header-templates']);
      },
      error: (error) => {
        console.error('Error saving template:', error);
        this.error = error.error?.message || 'Failed to save template. Please try again.';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/header-templates']);
  }

  getErrorMessage(fieldName: string): string {
    const control = this.templateForm.get(fieldName);
    if (control?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (control?.hasError('maxlength')) {
      const maxLength = control.errors?.['maxlength'].requiredLength;
      return `${fieldName} must not exceed ${maxLength} characters`;
    }
    return '';
  }

  insertPlaceholder(placeholder: string): void {
    const contentControl = this.templateForm.get('templateContent');
    if (contentControl) {
      const currentValue = contentControl.value || '';
      contentControl.setValue(currentValue + placeholder);
    }
  }

  get availablePlaceholders(): string[] {
    return [
      '{COMPANY_NAME}',
      '{ADDRESS}',
      '{PHONE}',
      '{EMAIL}',
      '{GST_NUMBER}',
      '{DATE}',
      '{INVOICE_NUMBER}',
      '{CLIENT_NAME}',
      '{CLIENT_ADDRESS}'
    ];
  }
}

// Made with Bob