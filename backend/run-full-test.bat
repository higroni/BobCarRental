@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File test-api-full.ps1
pause

@REM Made with Bob
