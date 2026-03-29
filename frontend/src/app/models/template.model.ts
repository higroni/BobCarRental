/**
 * Header Template model matching backend HeaderTemplate entity and DTOs
 */

export interface HeaderTemplate {
  id: number;
  templateName: string;
  templateContent: string;
  description?: string;
  active: boolean;
  isDefault: boolean;
  templateType: TemplateType;
  lineCount?: number;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface HeaderTemplateRequest {
  templateName: string;
  templateContent: string;
  description?: string;
  active: boolean;
  isDefault: boolean;
  templateType: TemplateType;
}

export interface HeaderTemplateResponse {
  id: number;
  templateName: string;
  templateContent: string;
  description?: string;
  active: boolean;
  isDefault: boolean;
  templateType: TemplateType;
  lineCount?: number;
  createdAt: string;
  updatedAt: string;
}

export enum TemplateType {
  INVOICE = 'INVOICE',
  CONFIRMATION_ORDER = 'CONFIRMATION_ORDER',
  RECEIPT = 'RECEIPT',
  STATEMENT = 'STATEMENT',
  CUSTOM = 'CUSTOM'
}

export const TEMPLATE_TYPE_LABELS: Record<TemplateType, string> = {
  [TemplateType.INVOICE]: 'Invoice',
  [TemplateType.CONFIRMATION_ORDER]: 'Confirmation Order',
  [TemplateType.RECEIPT]: 'Receipt',
  [TemplateType.STATEMENT]: 'Statement',
  [TemplateType.CUSTOM]: 'Custom'
};

// Made with Bob
