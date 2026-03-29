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

echo.
echo [2/4] Cleaning old build artifacts...
echo.
call mvn clean

echo.
echo [3/4] Compiling application...
echo.
call mvn compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ==========================================
    echo   ERROR: Compilation failed!
    echo ==========================================
    echo.
    pause
    exit /b 1
)

echo.
echo [4/4] Starting Spring Boot application...
echo.
echo ==========================================
echo   Application is starting...
echo   Press Ctrl+C to stop the application
echo ==========================================
echo.

REM Start the application
call mvn spring-boot:run

pause

@REM Made with Bob
