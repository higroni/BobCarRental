@echo off
echo Installing Puppeteer for E2E testing...
cd /d "%~dp0"
call npm install --save-dev puppeteer
echo.
echo Installation complete!
echo.
echo To run E2E test:
echo   node e2e-test.js
echo.
pause

@REM Made with Bob
