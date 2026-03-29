/**
 * Address models matching backend Address entity and DTOs
 */

export interface Address {
  id: number;
  clientId: string;
  dept?: string;
  desc?: string;
  name: string;
  address1?: string;
  address2?: string;
  address3?: string;
  place?: string;
  city?: string;
  pinCode?: number;
  phone?: string;
  fax?: string;
  tagged?: boolean;
  category?: string;
  companyName?: string;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface AddressRequest {
  clientId: string;
  dept?: string;
  desc?: string;
  name: string;
  address1?: string;
  address2?: string;
  address3?: string;
  place?: string;
  city?: string;
  pinCode?: number;
  phone?: string;
  fax?: string;
  category?: string;
  companyName?: string;
  isActive?: boolean;
}

export interface AddressResponse {
  id: number;
  clientId: string;
  dept?: string;
  desc?: string;
  name: string;
  address1?: string;
  address2?: string;
  address3?: string;
  place?: string;
  city?: string;
  pinCode?: number;
  phone?: string;
  fax?: string;
  tagged?: boolean;
  category?: string;
  companyName?: string;
  isActive?: boolean;
  createdAt: string;
  updatedAt: string;
}

// Made with Bob
