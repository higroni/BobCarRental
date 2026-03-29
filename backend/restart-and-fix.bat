@echo off
REM Restart Spring Boot and apply version column fix
REM This script will:
REM 1. Stop the running application
REM 2. Restart it (Liquibase will automatically apply changelog 016)

echo ========================================
echo Bob Car Rental - Restart and Fix Schema
echo ========================================
echo.

echo Step 1: Stopping Spring Boot application...
call stop.bat
timeout /t 3 /nobreak >nul

echo.
echo Step 2: Starting Spring Boot application...
echo Liquibase will automatically apply changelog 016-add-version-columns.yaml
echo This will add the 'version' column to all tables for optimistic locking.
echo.

start "Bob Car Rental Backend" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo Application is starting...
echo Watch the console window for:
echo   - "Liquibase: Update has been successful"
echo   - "Started BobCarRentalApplication"
echo ========================================
echo.
echo After successful startup, you can:
echo 1. Run migration: cd ..\migration && python migrate_dbf_to_h2.py
echo 2. Test APIs: test-api-full.bat
echo.
pause

@REM Made with Bob
