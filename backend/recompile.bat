@echo off
echo ========================================
echo  RECOMPILING BACKEND
echo ========================================
echo.
echo This will recompile the backend to regenerate MapStruct mappers
echo.

cd /d "%~dp0"

echo Cleaning and compiling...
call mvn clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  COMPILATION SUCCESSFUL
    echo ========================================
    echo.
    echo MapStruct mappers have been regenerated.
    echo You can now restart the application.
) else (
    echo.
    echo ========================================
    echo  COMPILATION FAILED
    echo ========================================
    echo.
    echo Check the error messages above.
)

pause

@REM Made with Bob
