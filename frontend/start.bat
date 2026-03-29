@echo off
echo Starting Angular Frontend with Proxy Configuration...
echo.
echo Backend should be running on http://localhost:8080
echo Frontend will be available on http://localhost:4200
echo.
echo Press Ctrl+C to stop the server
echo.

cd /d "%~dp0"
call npm start

pause

@REM Made with Bob
