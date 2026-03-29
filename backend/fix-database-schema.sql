-- Fix Database Schema for Legacy Data Migration
-- This script modifies the billings table to allow NULL values for trp_num and bill_amt
-- Required because legacy system allows billing without trip sheets

-- Alter billings table to allow NULL for trp_num
ALTER TABLE billings ALTER COLUMN trp_num INTEGER NULL;

-- Alter billings table to allow NULL for bill_amt  
ALTER TABLE billings ALTER COLUMN bill_amt DECIMAL(10,2) NULL;

-- Verify changes
SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'BILLINGS' 
AND COLUMN_NAME IN ('TRP_NUM', 'BILL_AMT');

-- Made with Bob
