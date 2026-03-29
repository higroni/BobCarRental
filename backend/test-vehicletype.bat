@echo off
echo Testing VehicleType Creation
echo ============================
echo.

REM Login first
echo 1. Logging in...
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin\"}" ^
  -o token.json
echo.

REM Extract token (simple approach - just show the file)
echo Token response saved to token.json
echo.

REM You need to manually copy the token and run:
echo 2. Create VehicleType (copy token from token.json first):
echo.
echo curl -X POST http://localhost:8080/api/v1/vehicle-types ^
echo   -H "Content-Type: application/json" ^
echo   -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
echo   -d "{\"typeId\":\"TEST\",\"typeName\":\"TEST VEHICLE\",\"tagged\":true}"
echo.

pause

@REM Made with Bob
