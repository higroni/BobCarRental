@echo off
echo ============================================================
echo DBF to H2 Migration Tool
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
python -c "import dbfread" >nul 2>&1
if errorlevel 1 (
    echo.
    echo Installing required packages...
    pip install -r requirements.txt
    if errorlevel 1 (
        echo ERROR: Failed to install packages!
        pause
        exit /b 1
    )
)

echo.
echo Starting migration...
echo.
python migrate_dbf_to_h2.py

echo.
echo ============================================================
echo Migration completed!
echo ============================================================
pause

@REM Made with Bob
