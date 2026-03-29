export interface BillingRequest {
  billNum: number;
  billDate: string;
  clientId: string;
  trpNum?: number;
  printed?: boolean;
  cancelled?: boolean;
  billAmount: number;
  billImg?: string;
  tagged?: boolean;
}

export interface BillingResponse {
  id: number;
  billNo: number;
  billDate: string;
  clientId: string;
  totalAmount: number;
  cgst: number;
  sgst: number;
  igst: number;
  paid: number;
  remarks?: string;
  tagged: boolean;
  billImg?: string;  // Generated bill content
  createdAt?: string;
  updatedAt?: string;
}

export interface BillingSummaryResponse {
  id: number;
  billNum: number;
  billDate: string;
  clientId: string;
  totalAmount: number;
  paid: number;
  tagged: boolean;
}

// Made with Bob
