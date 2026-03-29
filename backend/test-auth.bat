@echo off
REM Test Authentication API Endpoints
REM Make sure the application is running on http://localhost:8080

echo ========================================
echo Testing BobCarRental Authentication API
echo ========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: curl is not installed or not in PATH
    echo Please install curl or use Git Bash
    pause
    exit /b 1
)

echo [1/4] Testing Health Endpoint...
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo [2/4] Testing Login with admin/admin...
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin\"}" ^
  -w "\nHTTP Status: %%{http_code}\n"
echo.
echo.

echo [3/4] Testing Login with wrong password...
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"wrong\"}" ^
  -w "\nHTTP Status: %%{http_code}\n"
echo.
echo.

echo [4/4] Getting all clients (should fail without token)...
curl -X GET http://localhost:8080/api/v1/clients ^
  -H "Content-Type: application/json" ^
  -w "\nHTTP Status: %%{http_code}\n"
echo.
echo.

echo ========================================
echo Tests completed!
echo ========================================
pause

@REM Made with Bob
