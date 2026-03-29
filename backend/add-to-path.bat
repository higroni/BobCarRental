@echo off
echo ========================================
echo Add Java and Maven to PATH
echo ========================================
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo ERROR: This script must be run as Administrator!
    echo Right-click and select "Run as Administrator"
    echo.
    pause
    exit /b 1
)

echo This script will add Java and Maven to your system PATH.
echo.

REM Find Java installation
echo [1/2] Looking for Java installation...
set JAVA_HOME=
for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do set JAVA_HOME=%%i
if not defined JAVA_HOME (
    for /d %%i in ("C:\Program Files\Java\jdk-21*") do set JAVA_HOME=%%i
)
if not defined JAVA_HOME (
    for /d %%i in ("C:\Program Files\Temurin\jdk-21*") do set JAVA_HOME=%%i
)

if defined JAVA_HOME (
    echo Found Java at: %JAVA_HOME%
) else (
    echo Java 21 not found in standard locations.
    echo Please enter the full path to your Java installation:
    echo Example: C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot
    set /p JAVA_HOME="Java path: "
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: java.exe not found at %JAVA_HOME%\bin\
    echo Please check the path and try again.
    pause
    exit /b 1
)

REM Find Maven installation
echo.
echo [2/2] Looking for Maven installation...
set MAVEN_HOME=
for /d %%i in ("C:\Program Files\Apache\maven*") do set MAVEN_HOME=%%i
if not defined MAVEN_HOME (
    for /d %%i in ("C:\Program Files\Maven\apache-maven*") do set MAVEN_HOME=%%i
)
if not defined MAVEN_HOME (
    for /d %%i in ("C:\apache-maven*") do set MAVEN_HOME=%%i
)

if defined MAVEN_HOME (
    echo Found Maven at: %MAVEN_HOME%
) else (
    echo Maven not found in standard locations.
    echo Please enter the full path to your Maven installation:
    echo Example: C:\Program Files\Apache\maven\apache-maven-3.9.6
    set /p MAVEN_HOME="Maven path: "
)

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo ERROR: mvn.cmd not found at %MAVEN_HOME%\bin\
    echo Please check the path and try again.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Setting Environment Variables
echo ========================================
echo.

REM Set JAVA_HOME
echo Setting JAVA_HOME=%JAVA_HOME%
setx JAVA_HOME "%JAVA_HOME%" /M
if %errorLevel% neq 0 (
    echo ERROR: Failed to set JAVA_HOME
    pause
    exit /b 1
)
echo SUCCESS: JAVA_HOME set

REM Set MAVEN_HOME
echo.
echo Setting MAVEN_HOME=%MAVEN_HOME%
setx MAVEN_HOME "%MAVEN_HOME%" /M
if %errorLevel% neq 0 (
    echo ERROR: Failed to set MAVEN_HOME
    pause
    exit /b 1
)
echo SUCCESS: MAVEN_HOME set

REM Get current PATH
echo.
echo Adding to system PATH...
for /f "tokens=2*" %%a in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path') do set CURRENT_PATH=%%b

REM Check if Java already in PATH
echo %CURRENT_PATH% | find /i "%JAVA_HOME%\bin" >nul
if %errorLevel% equ 0 (
    echo Java bin already in PATH
) else (
    echo Adding Java bin to PATH...
    setx PATH "%CURRENT_PATH%;%JAVA_HOME%\bin" /M
    echo SUCCESS: Java bin added to PATH
)

REM Check if Maven already in PATH
echo %CURRENT_PATH% | find /i "%MAVEN_HOME%\bin" >nul
if %errorLevel% equ 0 (
    echo Maven bin already in PATH
) else (
    echo Adding Maven bin to PATH...
    for /f "tokens=2*" %%a in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path') do set CURRENT_PATH=%%b
    setx PATH "%CURRENT_PATH%;%MAVEN_HOME%\bin" /M
    echo SUCCESS: Maven bin added to PATH
)

echo.
echo ========================================
echo Verification
echo ========================================
echo.
echo Environment variables have been set!
echo.
echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%
echo.
echo IMPORTANT: You must restart your terminal/command prompt for changes to take effect!
echo.
echo After restarting, verify with:
echo   java -version
echo   mvn -version
echo.
echo ========================================
echo.
pause

@REM Made with Bob
