@echo off
echo ========================================
echo Bob Car Rental - Frontend Fix and Run
echo ========================================
echo.

echo [1/5] Installing @angular/animations (with legacy peer deps)...
call npm install @angular/animations@^21.2.0 --legacy-peer-deps
if errorlevel 1 (
    echo ERROR: Failed to install animations
    pause
    exit /b 1
)

echo.
echo [2/5] Installing other dependencies...
call npm install --legacy-peer-deps
if errorlevel 1 (
    echo ERROR: npm install failed
    pause
    exit /b 1
)

echo.
echo [3/5] Building frontend...
call npm run build
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [4/5] Running linter...
call npm run lint
if errorlevel 1 (
    echo WARNING: Linting issues found
)

echo.
echo [5/5] Starting development server...
echo Frontend will be available at: http://localhost:4200
echo Backend proxy configured for: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.
call npm start

pause

@REM Made with Bob
