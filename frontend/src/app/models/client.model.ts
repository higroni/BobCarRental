/**
 * Client model matching backend Client entity and DTOs
 */

export interface Client {
  id: number;
  clientId: string;
  clientName: string;
  address?: string;
  city?: string;
  phone?: string;
  mobile?: string;
  email?: string;
  gstNumber?: string;
  panNumber?: string;
  isActive: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface ClientRequest {
  clientId: string;
  clientName: string;
  address?: string;
  city?: string;
  phone?: string;
  mobile?: string;
  email?: string;
  gstNumber?: string;
  panNumber?: string;
  isActive: boolean;
}

export interface ClientResponse {
  id: number;
  clientId: string;
  clientName: string;
  address1?: string;
  address2?: string;
  address3?: string;
  place?: string;
  city?: string;
  pinCode?: number;
  phone?: string;
  fax?: string;
  fare?: string;
  isSplit?: boolean;
  tagged?: boolean;
  deleted?: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  updatedBy?: string;
}

// Made with Bob
