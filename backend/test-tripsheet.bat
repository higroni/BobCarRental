@echo off
echo Testing TripSheet API...
echo.

echo Step 1: Getting authentication token...
for /f "delims=" %%i in ('curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^| findstr /C:"token"') do set TOKEN_LINE=%%i
for /f "tokens=2 delims=:" %%a in ("%TOKEN_LINE%") do set TOKEN_WITH_QUOTES=%%a
set TOKEN=%TOKEN_WITH_QUOTES:"=%
set TOKEN=%TOKEN:,=%
set TOKEN=%TOKEN: =%

echo Token obtained: %TOKEN%
echo.

echo Step 2: Creating TripSheet with dates and times...
curl -X POST http://localhost:8080/api/tripsheets ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"trpNum\":9999,\"trpDate\":\"2024-01-15\",\"regNum\":\"TEST123\",\"driver\":\"Test Driver\",\"clientId\":\"CLI001\",\"startKm\":1000,\"endKm\":1500,\"typeId\":\"CAR\",\"startDate\":\"2024-01-15\",\"endDate\":\"2024-01-20\",\"startTime\":\"09:00\",\"endTime\":\"17:00\",\"status\":\"F\",\"hiring\":1000,\"extra\":200,\"halt\":150,\"minimum\":100,\"permit\":50,\"misc\":25}"

echo.
echo.
echo 2. Getting TripSheet by ID (replace {id} with actual ID from step 1)...
echo curl -X GET http://localhost:8080/api/tripsheets/{id} -H "Authorization: Bearer %TOKEN%"
echo.

pause

@REM Made with Bob
