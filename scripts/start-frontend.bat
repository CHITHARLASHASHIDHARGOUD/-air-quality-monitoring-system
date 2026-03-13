@echo off
REM ====================================
REM Urban Air Quality Monitoring System
REM Frontend Startup Script
REM ====================================

setlocal

echo.
echo ====================================
echo Starting Air Quality Frontend...
echo ====================================
echo.

REM Navigate to project root (parent of scripts directory)
set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
cd /d "%PROJECT_ROOT%"

if not exist "index.html" (
    echo ERROR: index.html not found in %CD%
    exit /b 1
)

REM Check if Python is available
python --version >nul 2>&1
if errorlevel 1 (
    echo Python is not installed or not in PATH
    echo Please install Python or open index.html directly in your browser
    echo.
    echo Opening index.html in default browser...
    start index.html
    pause
    exit /b
)

REM Start Python HTTP server
echo Starting HTTP server on port 5500...
echo.
echo Frontend will be available at:
echo   http://localhost:5500/index.html
echo   http://localhost:5500/air2.html
echo.
echo Press Ctrl+C to stop the server
echo.

python -m http.server 5500 --directory "%PROJECT_ROOT%"

pause
