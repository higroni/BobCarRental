@echo off
REM Restart application and apply audit columns changelog
REM This will add created_at, updated_at, created_by, updated_by to all tables

echo ========================================
echo Bob Car Rental - Apply Audit Columns
echo ========================================
echo.
echo This will add audit columns to all tables:
echo - created_at (TIMESTAMP, NOT NULL)
echo - updated_at (TIMESTAMP)
echo - created_by (VARCHAR(50))
echo - updated_by (VARCHAR(50))
echo.

echo Step 1: Stopping Spring Boot application...
call stop.bat
timeout /t 3 /nobreak >nul

echo.
echo Step 2: Starting Spring Boot application...
echo Liquibase will apply changelog 019-add-audit-columns.yaml
echo.

start "Bob Car Rental Backend" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo Application is starting...
echo Watch the console window for:
echo   - "Running Changeset: 019-add-audit-columns"
echo   - "Liquibase: Update has been successful"
echo   - "Started BobCarRentalApplication"
echo ========================================
echo.
echo After successful startup, you can run the migration script:
echo   cd ..\migration
echo   python migrate_dbf_to_h2.py
echo.
pause

@REM Made with Bob
