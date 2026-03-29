@echo off
REM Fix Database Schema Script
REM This script connects to H2 database and executes schema fixes

echo ========================================
echo Bob Car Rental - Database Schema Fix
echo ========================================
echo.
echo This script will modify the billings table to allow NULL values
echo for trp_num and bill_amt columns (required for legacy data migration)
echo.
echo Press Ctrl+C to cancel, or
pause

echo.
echo Connecting to H2 database...
echo.

REM Use H2's RunScript tool to execute SQL
java -cp "%USERPROFILE%\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar" org.h2.tools.RunScript -url "jdbc:h2:./data/bobcarrental" -user sa -password "" -script fix-database-schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Database schema updated successfully!
    echo ========================================
    echo.
    echo You can now restart the application and run the migration.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to update database schema
    echo ========================================
    echo.
    echo Please check:
    echo 1. H2 database file exists in ./data/bobcarrental.mv.db
    echo 2. Application is not running (stop it first)
    echo 3. H2 jar file is in Maven repository
)

echo.
pause

@REM Made with Bob
