@echo off
echo ========================================
echo BobCarRental - Prerequisites Installer
echo ========================================
echo.
echo This script will help you install:
echo 1. Java 21 (Temurin/Eclipse Adoptium)
echo 2. Apache Maven 3.9+
echo.
echo ========================================
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo WARNING: This script should be run as Administrator for best results.
    echo Right-click and select "Run as Administrator"
    echo.
    pause
)

echo Checking current installations...
echo.

REM Check Java
echo [1/2] Checking Java installation...
java -version >nul 2>&1
if %errorLevel% equ 0 (
    echo Java is already installed:
    java -version
    echo.
) else (
    echo Java is NOT installed.
    echo.
)

REM Check Maven
echo [2/2] Checking Maven installation...
mvn -version >nul 2>&1
if %errorLevel% equ 0 (
    echo Maven is already installed:
    mvn -version
    echo.
) else (
    echo Maven is NOT installed.
    echo.
)

echo ========================================
echo Installation Options
echo ========================================
echo.
echo OPTION 1: Install using Chocolatey (Recommended)
echo --------------------------------------------------
echo If you have Chocolatey installed, run these commands:
echo.
echo   choco install temurin21 -y
echo   choco install maven -y
echo.
echo If you don't have Chocolatey, install it from:
echo   https://chocolatey.org/install
echo.
echo.
echo OPTION 2: Manual Installation
echo --------------------------------------------------
echo.
echo Java 21 (Temurin):
echo   1. Download from: https://adoptium.net/temurin/releases/?version=21
echo   2. Choose: Windows x64 MSI installer
echo   3. Run installer and follow instructions
echo   4. Verify: java -version
echo.
echo Maven 3.9+:
echo   1. Download from: https://maven.apache.org/download.cgi
echo   2. Choose: Binary zip archive
echo   3. Extract to C:\Program Files\Apache\maven
echo   4. Add to PATH: C:\Program Files\Apache\maven\bin
echo   5. Verify: mvn -version
echo.
echo.
echo OPTION 3: Use Maven Wrapper (No Maven installation needed)
echo --------------------------------------------------
echo The project includes Maven Wrapper, so you can skip Maven installation
echo and use mvnw.cmd instead of mvn commands.
echo.
echo ========================================
echo.

choice /C 123 /M "Choose installation method (1=Chocolatey, 2=Manual, 3=Skip)"
set CHOICE=%errorlevel%

if %CHOICE%==1 (
    echo.
    echo Checking for Chocolatey...
    where choco >nul 2>&1
    if %errorLevel% equ 0 (
        echo Chocolatey found! Installing Java 21 and Maven...
        echo.
        choco install temurin21 -y
        choco install maven -y
        echo.
        echo Installation complete! Please restart your terminal.
    ) else (
        echo Chocolatey is not installed.
        echo.
        echo To install Chocolatey, run PowerShell as Administrator and execute:
        echo Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
        echo.
        echo Then run this script again.
    )
)

if %CHOICE%==2 (
    echo.
    echo Opening download pages in your browser...
    echo.
    start https://adoptium.net/temurin/releases/?version=21
    timeout /t 2 >nul
    start https://maven.apache.org/download.cgi
    echo.
    echo Please download and install manually, then run this script again to verify.
)

if %CHOICE%==3 (
    echo.
    echo Skipping installation. You can use Maven Wrapper (mvnw.cmd) instead.
    echo.
    echo To use Maven Wrapper:
    echo   mvnw.cmd clean install
    echo   mvnw.cmd spring-boot:run
    echo   mvnw.cmd test
)

echo.
echo ========================================
echo Verification
echo ========================================
echo.
echo Press any key to verify installations...
pause >nul

echo.
echo Checking Java...
java -version
if %errorLevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
) else (
    echo SUCCESS: Java is installed
)

echo.
echo Checking Maven...
mvn -version
if %errorLevel% neq 0 (
    echo WARNING: Maven is not installed or not in PATH
    echo You can use Maven Wrapper (mvnw.cmd) instead
) else (
    echo SUCCESS: Maven is installed
)

echo.
echo ========================================
echo Next Steps
echo ========================================
echo.
echo 1. Close and reopen your terminal
echo 2. Navigate to: bobcarrental/backend
echo 3. Run: mvnw.cmd clean install
echo 4. Run: mvnw.cmd spring-boot:run
echo 5. Run tests: mvnw.cmd test
echo.
echo For detailed instructions, see: SETUP_AND_RUN.md
echo.
pause

@REM Made with Bob
