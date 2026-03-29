@echo off
REM ============================================
REM BobCarRental - Restart Application Script
REM ============================================

echo.
echo ==========================================
echo   BobCarRental - Restart Application
echo ==========================================
echo.

REM Change to backend directory
cd /d "%~dp0"

echo [1/4] Stopping any running Spring Boot applications...
echo.

REM Kill any Java processes running Spring Boot
for /f "tokens=2" %%i in ('tasklist ^| findstr /i "java.exe"') do (
    echo Found Java process: %%i
    taskkill /F /PID %%i >nul 2>&1
)

REM Wait a moment for processes to terminate
timeout /t 2 /nobreak >nul

