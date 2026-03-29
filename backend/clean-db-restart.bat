@echo off
echo ============================================
echo Cleaning H2 Database and Restarting
echo ============================================
echo.

REM Stop the application if running
echo Stopping application...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

REM Delete H2 database files
echo Deleting H2 database files...
if exist "data\bobcarrental.mv.db" del /F /Q "data\bobcarrental.mv.db"
if exist "data\bobcarrental.trace.db" del /F /Q "data\bobcarrental.trace.db"
echo Database files deleted.
echo.

REM Start the application
echo Starting application...
start cmd /k "mvn spring-boot:run"

echo.
echo ============================================
echo Database cleaned and application starting
echo Wait for application to fully start...
echo ============================================

@REM Made with Bob
