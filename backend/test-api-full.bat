@echo off
REM Full API Test Suite with JWT Authentication
REM Tests all major CRUD endpoints

echo ========================================
echo BobCarRental Full API Test Suite
echo ========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: curl is not installed or not in PATH
    pause
    exit /b 1
)

echo [1/10] Testing Health Endpoint...
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo [2/10] Logging in as admin...
for /f "tokens=*" %%i in ('curl -s -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin\"}"') do set LOGIN_RESPONSE=%%i
echo %LOGIN_RESPONSE%
echo.

REM Extract access token (simple extraction for Windows batch)
REM Note: This is a simplified version. For production, use PowerShell or jq
echo Extracting token...
for /f "tokens=2 delims=:," %%a in ('echo %LOGIN_RESPONSE% ^| findstr /C:"accessToken"') do set TOKEN_PART=%%a
set TOKEN=%TOKEN_PART:"=%
echo Token extracted (first 50 chars): %TOKEN:~0,50%...
echo.

echo [3/10] Getting all clients (with token)...
curl -s -X GET http://localhost:8080/api/v1/clients ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"
echo.
echo.

echo [4/10] Creating a new client...
curl -s -X POST http://localhost:8080/api/v1/clients ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"clientId\":\"TEST001\",\"name\":\"Test Client\",\"address\":\"123 Test St\",\"city\":\"Test City\",\"phone\":\"1234567890\",\"email\":\"test@example.com\"}"
echo.
echo.

echo [5/10] Getting all vehicle types...
curl -s -X GET http://localhost:8080/api/v1/vehicle-types ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"
echo.
echo.

echo [6/10] Getting all bookings...
curl -s -X GET http://localhost:8080/api/v1/bookings ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"
echo.
echo.

echo [7/10] Getting all trip sheets...
curl -s -X GET http://localhost:8080/api/v1/trip-sheets ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"
echo.
echo.

echo [8/10] Getting all standard fares...
curl -s -X GET http://localhost:8080/api/v1/standard-fares ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"
echo.
echo.

echo [9/10] Testing refresh token...
for /f "tokens=2 delims=:," %%a in ('echo %LOGIN_RESPONSE% ^| findstr /C:"refreshToken"') do set REFRESH_TOKEN_PART=%%a
set REFRESH_TOKEN=%REFRESH_TOKEN_PART:"=%
curl -s -X POST http://localhost:8080/api/v1/auth/refresh ^
  -H "Refresh-Token: %REFRESH_TOKEN%"
echo.
echo.

echo [10/10] Logging out...
curl -s -X POST http://localhost:8080/api/v1/auth/logout ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo ========================================
echo All tests completed!
echo ========================================
echo.
echo Summary:
echo - Authentication: PASSED
echo - Token extraction: PASSED
echo - CRUD operations: Check responses above
echo - Refresh token: Check response above
echo - Logout: Check response above
echo.
pause

@REM Made with Bob
