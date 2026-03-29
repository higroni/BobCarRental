@echo off
echo Getting TripSheet 9999...
echo.

echo Step 1: Getting authentication token...
for /f "delims=" %%i in ('curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^| findstr /C:"token"') do set TOKEN_LINE=%%i
for /f "tokens=2 delims=:" %%a in ("%TOKEN_LINE%") do set TOKEN_WITH_QUOTES=%%a
set TOKEN=%TOKEN_WITH_QUOTES:"=%
set TOKEN=%TOKEN:,=%
set TOKEN=%TOKEN: =%

echo Token obtained
echo.

echo Step 2: Getting TripSheet 9999...
curl -X GET http://localhost:8080/api/tripsheets/9999 -H "Authorization: Bearer %TOKEN%" -H "Accept: application/json"

echo.
echo.
pause

@REM Made with Bob
