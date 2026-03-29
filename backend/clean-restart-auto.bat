@echo off
REM Clean Database and Restart Application (Automated - No Prompts)
REM This script deletes the H2 database and restarts the application

echo ========================================
echo Bob Car Rental - Clean Database Restart
echo ========================================
echo.

echo Step 1: Stopping Spring Boot application...
call stop.bat
timeout /t 3 /nobreak >nul

echo.
echo Step 2: Deleting H2 database files...
if exist data\bobcarrental.mv.db (
    del data\bobcarrental.mv.db
    echo Database file deleted successfully
) else (
    echo Database file not found (already deleted or never created)
)

if exist data\bobcarrental.trace.db (
    del data\bobcarrental.trace.db
    echo Trace file deleted
)

echo.
echo Step 3: Starting Spring Boot application...
echo This will recreate the database with correct schema
echo.
start "Bob Car Rental Backend" mvn spring-boot:run

echo.
echo ========================================
echo Database cleaned and application starting
echo ========================================
echo.
echo Wait for "Started BobCarRentalApplication" message
echo Then you can run the migration script
echo.

@REM Made with Bob