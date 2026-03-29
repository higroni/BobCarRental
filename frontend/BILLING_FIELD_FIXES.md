# Billing Frontend Field Name Fixes

## Problem
Frontend Billing komponente koristile su potpuno drugačija imena polja od backend API-ja. Backend očekuje `billAmount` ali frontend je slao `totalAmount`, `paidAmount`, `balanceAmount`, itd.

## Backend API Structure

### BillingRequest (što backend očekuje):
```java
{
  billNum: Integer (required)
  billDate: LocalDate (required)
  clientId: String (required)
  trpNum: Integer (optional)
  printed: Boolean (default: false)
  cancelled: Boolean (default: false)
  billAmount: BigDecimal (required) ← KLJUČNO POLJE
  billImg: String (optional)
  tagged: Boolean (default: false)
}
```

### BillingResponse (što backend vraća):
```java
{
  id: Long
  billNo: Integer
  billDate: LocalDate
  clientId: String
  totalAmount: BigDecimal
  cgst: BigDecimal
  sgst: BigDecimal
  igst: BigDecimal
  paid: BigDecimal
  remarks: String
  tagged: Boolean
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

### BillingSummaryResponse (za listu):
```java
{
  id: Long
  billNum: Integer
  billDate: LocalDate
  clientId: String
  totalAmount: BigDecimal
  paid: BigDecimal
  tagged: Boolean
}
```

## Fixed Files

### 1. `src/app/models/billing.model.ts`

**BillingRequest - Before:**
```typescript
{
  totalAmount: number;
  paidAmount?: number;
  balanceAmount?: number;
  paymentMode?: string;
  remarks?: string;
  tripSheetIds?: number[];
}
```

**BillingRequest - After:**
```typescript
{
  billNum: number;
  billDate: string;
  clientId: string;
  trpNum?: number;
  printed?: boolean;
  cancelled?: boolean;
  billAmount: number;  // ← Changed from totalAmount
  billImg?: string;
  tagged?: boolean;
}
```

**BillingResponse - Before:**
```typescript
{
  billNum: number;
  clientName?: string;
  totalAmount: number;
  paidAmount: number;
  balanceAmount: number;
  paymentMode?: string;
  tripSheetCount?: number;
}
```

**BillingResponse - After:**
```typescript
{
  billNo: number;  // ← Changed from billNum
  totalAmount: number;
  cgst: number;
  sgst: number;
  igst: number;
  paid: number;  // ← Changed from paidAmount
  remarks?: string;
  tagged: boolean;
}
```

**BillingSummaryResponse - Before:**
```typescript
{
  clientName?: string;
  paidAmount: number;
  balanceAmount: number;
  tripSheetCount?: number;
  isPaid: boolean;
}
```

**BillingSummaryResponse - After:**
```typescript
{
  clientId: string;  // ← Changed from clientName
  paid: number;  // ← Changed from paidAmount
  tagged: boolean;
  // Balance calculated: totalAmount - paid
}
```

### 2. `src/app/features/billings/billing-form/billing-form.ts`

**FormGroup - Changed:**
- `totalAmount` → `billAmount`
- Removed: `paidAmount`, `balanceAmount`, `paymentMode`, `remarks`
- Added: `trpNum`, `printed`, `cancelled`, `billImg`, `tagged`

**Request Mapping - Changed:**
```typescript
// Before
{
  totalAmount: formValue.totalAmount,
  paidAmount: formValue.paidAmount || 0,
  balanceAmount: formValue.balanceAmount || 0,
  paymentMode: formValue.paymentMode,
  remarks: formValue.remarks,
  tripSheetIds: this.selectedTripSheets
}

// After
{
  billAmount: formValue.billAmount,
  trpNum: formValue.trpNum,
  printed: formValue.printed || false,
  cancelled: formValue.cancelled || false,
  billImg: formValue.billImg,
  tagged: formValue.tagged || false
}
```

**Response Mapping - Changed:**
```typescript
// Before
billNum: billing.billNum,
totalAmount: billing.totalAmount,
paidAmount: billing.paidAmount,
balanceAmount: billing.balanceAmount

// After
billNum: billing.billNo,  // ← Response uses billNo
billAmount: billing.totalAmount,  // ← Map to form field
tagged: billing.tagged
```

### 3. `src/app/features/billings/billing-form/billing-form.html`

**Changed Fields:**
- `totalAmount` → `billAmount`
- Removed: Paid Amount, Balance Amount, Payment Mode, Remarks
- Added: Trip Number, Printed checkbox, Cancelled checkbox, Tagged checkbox, Bill Image textarea

### 4. `src/app/features/billings/billing-list/billing-list.ts`

**displayedColumns - Changed:**
```typescript
// Before
['billNum', 'billDate', 'clientName', 'totalAmount', 'paidAmount', 'balanceAmount', 'tripSheetCount', 'status', 'actions']

// After
['billNum', 'billDate', 'clientId', 'totalAmount', 'paid', 'balance', 'tagged', 'status', 'actions']
```

**Methods Added:**
```typescript
getBalance(billing: BillingSummaryResponse): number {
  return (billing.totalAmount || 0) - (billing.paid || 0);
}
```

**Status Logic - Changed:**
```typescript
// Before
billing.isPaid ? 'Paid' : 'Unpaid'

// After
const balance = (billing.totalAmount || 0) - (billing.paid || 0);
return balance <= 0 ? 'Paid' : 'Unpaid';
```

### 5. `src/app/features/billings/billing-list/billing-list.html`

**Column Changes:**
- `clientName` → `clientId`
- `paidAmount` → `paid`
- `balanceAmount` → `balance` (calculated via `getBalance()`)
- `tripSheetCount` → `tagged` (with icon display)

## Result

✅ **Create Billing** - Now sends correct `billAmount` field
✅ **Edit Billing** - Properly maps response fields
✅ **List Billings** - Displays correct data from backend
✅ **Balance Calculation** - Computed from `totalAmount - paid`
✅ **Status Display** - Based on balance calculation

## Testing

After these fixes, test by:
1. Navigate to Billings list
2. Click "New Billing"
3. Fill in Bill Number, Date, Client, and Bill Amount
4. Click Save - should succeed without validation errors
5. Verify billing appears in list with correct data
6. Click Edit - verify all fields populate correctly

## Related Backend Files
- `BillingRequest.java` - Request DTO with `billAmount`
- `BillingResponse.java` - Response DTO with `billNo`, `totalAmount`, `paid`
- `BillingSummaryResponse.java` - Summary DTO for list view
- `Billing.java` - Entity with both `billAmt` and `totalAmount` fields