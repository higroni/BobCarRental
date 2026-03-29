import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { HeaderTemplateService } from '../../../core/services/header-template.service';
import { HeaderTemplateResponse, TEMPLATE_TYPE_LABELS } from '../../../models';

@Component({
  selector: 'app-header-template-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './header-template-list.html',
  styleUrls: ['./header-template-list.css']
})
export class HeaderTemplateListComponent implements OnInit {
  templates: HeaderTemplateResponse[] = [];
  displayedColumns: string[] = ['templateName', 'templateType', 'lineCount', 'active', 'isDefault', 'actions'];
  loading = false;
  error: string | null = null;

  constructor(
    private headerTemplateService: HeaderTemplateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTemplates();
  }

  loadTemplates(): void {
    this.loading = true;
    this.error = null;
    
    this.headerTemplateService.getHeaderTemplates().subscribe({
      next: (templates) => {
        this.templates = templates;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading header templates:', error);
        this.error = 'Failed to load header templates. Please try again.';
        this.loading = false;
      }
    });
  }

  getTemplateTypeLabel(type: string): string {
    return TEMPLATE_TYPE_LABELS[type as keyof typeof TEMPLATE_TYPE_LABELS] || type;
  }

  createTemplate(): void {
    this.router.navigate(['/header-templates/new']);
  }

  editTemplate(id: number): void {
    this.router.navigate(['/header-templates', id, 'edit']);
  }

  setAsDefault(template: HeaderTemplateResponse): void {
    if (template.isDefault) {
      return; // Already default
    }

    this.headerTemplateService.setAsDefault(template.id).subscribe({
      next: () => {
        this.loadTemplates(); // Reload to update default status
      },
      error: (error) => {
        console.error('Error setting default template:', error);
        this.error = 'Failed to set default template. Please try again.';
      }
    });
  }

  deleteTemplate(template: HeaderTemplateResponse): void {
    if (!confirm(`Are you sure you want to delete template "${template.templateName}"?`)) {
      return;
    }

    this.headerTemplateService.deleteHeaderTemplate(template.id).subscribe({
      next: () => {
        this.loadTemplates();
      },
      error: (error) => {
        console.error('Error deleting template:', error);
        this.error = 'Failed to delete template. Please try again.';
      }
    });
  }

  viewTemplate(template: HeaderTemplateResponse): void {
    // Show template content in a dialog or navigate to view page
    alert(`Template Content:\n\n${template.templateContent}`);
  }
}

// Made with Bob