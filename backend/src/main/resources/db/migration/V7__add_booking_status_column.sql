-- Add status column to bookings table
-- Migration for booking workflow: PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
-- Can be CANCELLED at any stage

ALTER TABLE bookings ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';

-- Update existing bookings to have CONFIRMED status (legacy default)
-- Cancelled bookings (deleted=true) will show CANCELLED via getStatus() method
UPDATE bookings SET status = 'CONFIRMED' WHERE deleted = false OR deleted IS NULL;

-- Add index for status queries
CREATE INDEX idx_booking_status ON bookings(status);

-- Add comment
COMMENT ON COLUMN bookings.status IS 'Booking workflow status: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED';

-- Made with Bob
