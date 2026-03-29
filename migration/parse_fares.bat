@echo off
echo ============================================================
echo FARES.TXT and HEADER.TXT Parser
echo ============================================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH!
    echo Please install Python 3.7+ from https://www.python.org/
    pause
    exit /b 1
)

echo Python found!
echo.

REM Check if required packages are installed
echo Checking required packages...
python -c "import requests" >nul 2>&1
if errorlevel 1 (
    echo.
    echo Installing required packages...
    pip install requests
    if errorlevel 1 (
        echo ERROR: Failed to install packages!
        pause
        exit /b 1
    )
)

echo.
echo Starting parser...
echo.
python parse_fares_and_headers.py

echo.
echo ============================================================
echo Parsing completed!
echo ============================================================
pause

@REM Made with Bob
