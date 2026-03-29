/**
 * Account model interfaces matching backend DTOs
 */

export interface Account {
  id?: number;
  desc: string;
  num?: number;
  date: string; // LocalDate as ISO string
  clientId: string;
  recd?: number;
  bill?: number;
  tagged?: boolean;
  code?: string;
  accountType?: string;
  name?: string;
  currentBalance?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AccountRequest {
  desc: string;
  num?: number;
  date: string; // LocalDate as ISO string (YYYY-MM-DD)
  clientId: string;
  amount: number;
  tagged?: boolean;
}

export interface AccountResponse {
  id: number;
  desc: string;
  num: number;
  date: string;
  clientId: string;
  recd: number;
  bill: number;
  tagged: boolean;
  code: string;
  accountType: string;
  name: string;
  currentBalance: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

// Made with Bob
