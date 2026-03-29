@echo off
echo ========================================
echo  RESTARTING ANGULAR WITH PROXY
echo ========================================
echo.
echo This will:
echo 1. Kill any running ng serve processes
echo 2. Start Angular dev server with proxy configuration
echo.
echo Backend must be running on http://localhost:8080
echo.
pause

echo.
echo Killing existing Angular processes...
taskkill /F /IM node.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Starting Angular dev server with proxy...
echo.
cd /d "%~dp0"

echo Proxy configuration will forward /api/* to http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

call ng serve --proxy-config proxy.conf.json

pause

@REM Made with Bob
