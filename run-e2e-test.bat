@echo off
echo ========================================
echo Bob Car Rental - E2E Test Runner
echo ========================================
echo.

REM Check if backend is running
echo [1/3] Checking backend status...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Backend is NOT running!
    echo Please start backend first: cd backend ^&^& mvnw spring-boot:run
    echo.
    pause
    exit /b 1
)
echo ✓ Backend is running

REM Check if frontend is running
echo.
echo [2/3] Checking frontend status...
curl -s http://localhost:4200 >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Frontend is NOT running!
    echo Please start frontend first: cd frontend ^&^& npm start
    echo.
    pause
    exit /b 1
)
echo ✓ Frontend is running

REM Run E2E test
echo.
echo [3/3] Running E2E test...
echo.
cd frontend
node e2e-test.js

echo.
echo ========================================
echo Test completed!
echo ========================================
echo.
echo Check test-screenshots folder for results
echo.
pause

@REM Made with Bob
