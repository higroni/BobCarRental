@echo off
echo ========================================
echo Starting Bob Car Rental Application
echo ========================================
echo.

REM Check if backend is already running
echo Checking if backend is running...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Backend is already running
) else (
    echo Starting backend...
    start "Backend Server" cmd /c "cd backend && mvnw.cmd spring-boot:run"
    echo Waiting for backend to start...
    timeout /t 10 /nobreak >nul
)

REM Check if frontend is already running
echo.
echo Checking if frontend is running...
curl -s http://localhost:4200 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Frontend is already running
) else (
    echo Starting frontend...
    start "Frontend Server" cmd /c "cd frontend && npm start"
    echo Waiting for frontend to start...
    timeout /t 15 /nobreak >nul
)

echo.
echo ========================================
echo Application started!
echo ========================================
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:4200
echo.
echo Login credentials:
echo Username: admin
echo Password: admin123
echo.
echo Press any key to run E2E test...
pause >nul

echo.
echo Running E2E test...
cd frontend
node e2e-test.js

echo.
echo Press any key to exit...
pause >nul

@REM Made with Bob
