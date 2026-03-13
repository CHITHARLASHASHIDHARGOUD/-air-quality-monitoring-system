@echo off
REM ====================================
REM Stop All Services Script
REM ====================================

echo.
echo ====================================
echo Stopping All Services...
echo ====================================
echo.

REM Navigate to project root
cd /d "%~dp0"

REM Stop Docker containers if running
echo Checking for Docker containers...
docker-compose ps >nul 2>&1
if not errorlevel 1 (
    echo Stopping Docker containers...
    docker-compose down
    echo Docker containers stopped.
    echo.
)

REM Kill Java processes (backend)
echo Stopping Java backend processes...
tasklist | find /i "java.exe" >nul 2>&1
if not errorlevel 1 (
    echo Found running Java processes
    taskkill /F /IM java.exe >nul 2>&1
    if not errorlevel 1 (
        echo Backend stopped.
    )
) else (
    echo No Java backend processes found.
)
echo.

REM Kill Python processes (frontend)
echo Stopping Python frontend server...
tasklist | find /i "python.exe" >nul 2>&1
if not errorlevel 1 (
    for /f "tokens=2" %%a in ('netstat -ano ^| find ":5500"') do (
        taskkill /F /PID %%a >nul 2>&1
    )
    echo Frontend server stopped.
) else (
    echo No Python server found.
)
echo.

echo ====================================
echo All services stopped!
echo ====================================
echo.

pause
