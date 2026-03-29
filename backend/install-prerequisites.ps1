# BobCarRental - Prerequisites Installer (PowerShell)
# Run as Administrator for best results

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BobCarRental - Prerequisites Installer" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "This script will install:" -ForegroundColor Yellow
Write-Host "1. Java 21 (Temurin/Eclipse Adoptium)" -ForegroundColor White
Write-Host "2. Apache Maven 3.9+" -ForegroundColor White
Write-Host ""

# Check if running as administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "WARNING: Not running as Administrator" -ForegroundColor Red
    Write-Host "Some installations may fail. Right-click and 'Run as Administrator'" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "Checking current installations..." -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-Host "[1/2] Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java is already installed:" -ForegroundColor Green
    Write-Host $javaVersion[0] -ForegroundColor White
    $javaInstalled = $true
} catch {
    Write-Host "Java is NOT installed." -ForegroundColor Red
    $javaInstalled = $false
}
Write-Host ""

# Check Maven
Write-Host "[2/2] Checking Maven installation..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "Maven is already installed:" -ForegroundColor Green
    Write-Host $mavenVersion[0] -ForegroundColor White
    $mavenInstalled = $true
} catch {
    Write-Host "Maven is NOT installed." -ForegroundColor Red
    $mavenInstalled = $false
}
Write-Host ""

# Check Chocolatey
Write-Host "Checking for Chocolatey package manager..." -ForegroundColor Yellow
try {
    $chocoVersion = choco -v 2>&1
    Write-Host "Chocolatey is installed: $chocoVersion" -ForegroundColor Green
    $chocoInstalled = $true
} catch {
    Write-Host "Chocolatey is NOT installed." -ForegroundColor Red
    $chocoInstalled = $false
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Installation Options" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($chocoInstalled) {
    Write-Host "OPTION 1: Install using Chocolatey (Recommended)" -ForegroundColor Green
    Write-Host "--------------------------------------------------" -ForegroundColor Gray
    Write-Host ""
    
    $install = Read-Host "Do you want to install Java 21 and Maven using Chocolatey? (Y/N)"
    
    if ($install -eq "Y" -or $install -eq "y") {
        Write-Host ""
        Write-Host "Installing prerequisites..." -ForegroundColor Yellow
        Write-Host ""
        
        if (-not $javaInstalled) {
            Write-Host "Installing Java 21 (Temurin)..." -ForegroundColor Yellow
            choco install temurin21 -y
            Write-Host "Java 21 installed!" -ForegroundColor Green
        } else {
            Write-Host "Java already installed, skipping..." -ForegroundColor Yellow
        }
        
        Write-Host ""
        
        if (-not $mavenInstalled) {
            Write-Host "Installing Maven..." -ForegroundColor Yellow
            choco install maven -y
            Write-Host "Maven installed!" -ForegroundColor Green
        } else {
            Write-Host "Maven already installed, skipping..." -ForegroundColor Yellow
        }
        
        Write-Host ""
        Write-Host "Installation complete!" -ForegroundColor Green
        Write-Host "Please restart your terminal for changes to take effect." -ForegroundColor Yellow
    }
} else {
    Write-Host "Chocolatey is not installed." -ForegroundColor Red
    Write-Host ""
    Write-Host "OPTION 1: Install Chocolatey first (Recommended)" -ForegroundColor Yellow
    Write-Host "--------------------------------------------------" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Run this command in PowerShell (as Administrator):" -ForegroundColor White
    Write-Host ""
    Write-Host 'Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString("https://community.chocolatey.org/install.ps1"))' -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Then run this script again." -ForegroundColor Yellow
    Write-Host ""
    Write-Host ""
    Write-Host "OPTION 2: Manual Installation" -ForegroundColor Yellow
    Write-Host "--------------------------------------------------" -ForegroundColor Gray
    Write-Host ""
    
    $manual = Read-Host "Do you want to open download pages for manual installation? (Y/N)"
    
    if ($manual -eq "Y" -or $manual -eq "y") {
        Write-Host ""
        Write-Host "Opening download pages..." -ForegroundColor Yellow
        Start-Process "https://adoptium.net/temurin/releases/?version=21"
        Start-Sleep -Seconds 2
        Start-Process "https://maven.apache.org/download.cgi"
        Write-Host ""
        Write-Host "Please download and install manually:" -ForegroundColor Yellow
        Write-Host "1. Java 21 - Choose Windows x64 MSI installer" -ForegroundColor White
        Write-Host "2. Maven - Choose Binary zip archive" -ForegroundColor White
        Write-Host "3. Extract Maven to C:\Program Files\Apache\maven" -ForegroundColor White
        Write-Host "4. Add to PATH: C:\Program Files\Apache\maven\bin" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Verification" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$verify = Read-Host "Do you want to verify installations now? (Y/N)"

if ($verify -eq "Y" -or $verify -eq "y") {
    Write-Host ""
    Write-Host "Verifying Java..." -ForegroundColor Yellow
    try {
        java -version
        Write-Host "SUCCESS: Java is installed" -ForegroundColor Green
    } catch {
        Write-Host "ERROR: Java is not installed or not in PATH" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Verifying Maven..." -ForegroundColor Yellow
    try {
        mvn -version
        Write-Host "SUCCESS: Maven is installed" -ForegroundColor Green
    } catch {
        Write-Host "WARNING: Maven is not installed or not in PATH" -ForegroundColor Yellow
        Write-Host "You can use Maven Wrapper (mvnw.cmd) instead" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Next Steps" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Close and reopen your terminal" -ForegroundColor White
Write-Host "2. Navigate to: bobcarrental/backend" -ForegroundColor White
Write-Host "3. Run: .\mvnw.cmd clean install" -ForegroundColor Cyan
Write-Host "4. Run: .\mvnw.cmd spring-boot:run" -ForegroundColor Cyan
Write-Host "5. Run tests: .\mvnw.cmd test" -ForegroundColor Cyan
Write-Host ""
Write-Host "For detailed instructions, see: SETUP_AND_RUN.md" -ForegroundColor Yellow
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Made with Bob
