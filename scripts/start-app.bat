@echo off
REM ====================================
REM Urban Air Quality Monitoring System
REM Complete Application Startup
REM ====================================

echo.
echo ========================================
echo Urban Air Quality Monitoring System
echo Complete Application Startup
echo ========================================
echo.

REM Set colors
color 0A

echo This will start both Backend and Frontend services
echo.
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5500/index.html
echo.
echo Starting automatically in non-interactive mode...

REM Start Backend in new window
echo.
echo [1/2] Starting Backend Server...
start "Air Quality Backend" cmd /k "%~dp0start-backend.bat"

REM Wait a moment for backend to initialize
echo Waiting 5 seconds for backend to initialize...
timeout /t 5 /nobreak >nul

REM Start Frontend in new window
echo.
echo [2/2] Starting Frontend Server...
start "Air Quality Frontend" cmd /k "%~dp0start-frontend.bat"

echo.
echo ========================================
echo Both services are starting!
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5500/index.html
echo.
echo Check the opened terminal windows for logs
echo.
echo Opening dashboard in browser...
timeout /t 3 /nobreak >nul

REM Open browser
start http://localhost:5500/index.html

echo.
echo Application started successfully!
echo Close the terminal windows to stop the services
echo.
