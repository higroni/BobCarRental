@echo off
echo ========================================
echo BobCarRental Backend - Running Tests
echo ========================================
echo.

cd /d "%~dp0"

REM Check if Maven wrapper exists
if exist "mvnw.cmd" (
    echo Using Maven Wrapper...
    call mvnw.cmd clean test
) else if exist "mvnw" (
    echo Using Maven Wrapper...
    call mvnw clean test
) else (
    echo Maven wrapper not found. Checking for Maven installation...
    where mvn >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo Using system Maven...
        mvn clean test
    ) else (
        echo ERROR: Maven is not installed and Maven wrapper is not available.
        echo.
        echo Please install Maven or run: mvn -N io.takari:maven:wrapper
        echo.
        pause
        exit /b 1
    )
)

echo.
echo ========================================
echo Tests completed!
echo ========================================
pause

@REM Made with Bob
