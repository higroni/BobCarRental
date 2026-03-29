# Booking Status Workflow

## Overview
This document describes the booking status workflow implementation for the Bob Car Rental system.

## Status Flow

```
PENDING → CONFIRMED → COMPLETED
   ↓          ↓           ↓
CANCELLED  CANCELLED  (cannot cancel)
```

### Status Definitions

1. **PENDING** (Default for new bookings)
   - Initial status when booking is created
   - Awaiting admin confirmation
   - Can be: Confirmed, Cancelled, Edited, Deleted

2. **CONFIRMED** (After admin confirms)
   - Booking has been verified and approved
   - Ready for execution
   - Can be: Completed, Cancelled, Edited

3. **IN_PROGRESS** (Optional intermediate state)
   - Booking is currently being executed
   - Can be: Completed, Cancelled

4. **COMPLETED** (Final success state)
   - Booking has been successfully fulfilled
   - Cannot be cancelled or edited
   - Can only be deleted by admin

5. **CANCELLED** (Final cancellation state)
   - Booking has been cancelled
   - Uses legacy `deleted` flag (deleted=true)
   - Can only be deleted by admin

## Database Schema

### Status Column
```sql
ALTER TABLE bookings ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
```

### Legacy Compatibility
- `deleted` flag (boolean) - Used for cancellation (legacy system)
- When `deleted=true`, `getStatus()` returns "CANCELLED" regardless of status column
- This maintains backward compatibility with legacy DBF system

## API Endpoints

### Workflow Actions

1. **Confirm Booking** (ADMIN only)
   ```
   PATCH /api/v1/bookings/{id}/confirm
   ```
   - Changes status: PENDING → CONFIRMED
   - Validates: Only PENDING bookings can be confirmed

2. **Complete Booking** (ADMIN only)
   ```
   PATCH /api/v1/bookings/{id}/complete
   ```
   - Changes status: CONFIRMED → COMPLETED (or IN_PROGRESS → COMPLETED)
   - Validates: Cannot complete CANCELLED bookings

3. **Cancel Booking** (ADMIN or USER)
   ```
   PATCH /api/v1/bookings/{id}/cancel
   ```
   - Sets `deleted=true` (legacy flag)
   - Status becomes CANCELLED via getStatus() override
   - Validates: Cannot cancel COMPLETED bookings

## Frontend Implementation

### Action Buttons Visibility

| Action   | Visible When                    | Role Required |
|----------|--------------------------------|---------------|
| View     | Always                         | USER/ADMIN    |
| Edit     | PENDING or CONFIRMED           | USER/ADMIN    |
| Confirm  | PENDING                        | ADMIN         |
| Complete | CONFIRMED or IN_PROGRESS       | ADMIN         |
| Cancel   | Not CANCELLED and not COMPLETED| USER/ADMIN    |
| Delete   | CANCELLED                      | ADMIN         |

### Status Badge Colors

```typescript
PENDING     → Yellow/Warning
CONFIRMED   → Blue/Primary
IN_PROGRESS → Purple/Accent
COMPLETED   → Green/Success
CANCELLED   → Red/Error
```

## Migration Notes

### V7__add_booking_status_column.sql

1. Adds `status` column with default 'PENDING'
2. Updates existing bookings to 'CONFIRMED' (legacy default)
3. Cancelled bookings (deleted=true) show 'CANCELLED' via getStatus()
4. Adds index on status column for performance

### Backward Compatibility

- Existing bookings get 'CONFIRMED' status (maintains current behavior)
- `deleted` flag still works for cancellation (legacy compatibility)
- `getStatus()` method handles both new status column and legacy deleted flag

## Business Rules

1. **New Bookings**
   - Created with status = 'PENDING'
   - Require admin confirmation before execution

2. **Confirmation**
   - Only admins can confirm bookings
   - Moves booking from PENDING to CONFIRMED

3. **Completion**
   - Only admins can complete bookings
   - Can complete from CONFIRMED or IN_PROGRESS
   - Automatically transitions through IN_PROGRESS if needed

4. **Cancellation**
   - Users can cancel their own bookings
   - Admins can cancel any booking
   - Cannot cancel completed bookings
   - Sets legacy `deleted` flag for compatibility

5. **Deletion**
   - Only admins can delete bookings
   - Only cancelled bookings can be deleted
   - Permanent removal from database

## Testing Checklist

- [ ] Create new booking → Status is PENDING
- [ ] Confirm PENDING booking → Status becomes CONFIRMED
- [ ] Complete CONFIRMED booking → Status becomes COMPLETED
- [ ] Cancel PENDING booking → Status becomes CANCELLED
- [ ] Cancel CONFIRMED booking → Status becomes CANCELLED
- [ ] Try to cancel COMPLETED booking → Should fail
- [ ] Try to confirm non-PENDING booking → Should fail
- [ ] Delete CANCELLED booking → Should succeed
- [ ] Try to delete non-CANCELLED booking → Should fail
- [ ] Edit PENDING booking → Should succeed
- [ ] Edit CONFIRMED booking → Should succeed
- [ ] Try to edit COMPLETED booking → Should fail
- [ ] Try to edit CANCELLED booking → Should fail

## Code References

### Backend
- Entity: `com.bobcarrental.model.Booking`
- Service: `com.bobcarrental.service.impl.BookingServiceImpl`
- Controller: `com.bobcarrental.controller.BookingController`
- Migration: `V7__add_booking_status_column.sql`

### Frontend
- Component: `booking-list.component.ts`
- Service: `booking.service.ts`
- Template: `booking-list.component.html`

## Future Enhancements

1. **Email Notifications**
   - Send email when booking is confirmed
   - Send email when booking is completed
   - Send email when booking is cancelled

2. **Status History**
   - Track status changes with timestamps
   - Show who made each status change
   - Audit trail for compliance

3. **Automated Status Transitions**
   - Auto-confirm bookings after X hours
   - Auto-complete bookings after end date
   - Send reminders for pending confirmations

4. **Advanced Workflow**
   - Add PENDING_PAYMENT status
   - Add PAYMENT_CONFIRMED status
   - Integrate with payment gateway