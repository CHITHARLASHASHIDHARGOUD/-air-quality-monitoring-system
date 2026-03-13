@echo off
REM ====================================
REM Docker Deployment Script
REM ====================================

echo.
echo ====================================
echo Docker Deployment
echo ====================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not installed or not in PATH
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo Docker version:
docker --version
docker-compose --version
echo.

echo This will:
echo  1. Build Docker images
echo  2. Start all containers
echo  3. Make the application available at http://localhost
echo.
echo Press any key to continue or Ctrl+C to cancel...
pause >nul

REM Navigate to project root
cd /d "%~dp0"

REM Stop any running containers
echo.
echo Stopping any running containers...
docker-compose down

REM Build and start
echo.
echo Building and starting containers...
docker-compose up -d --build

REM Check status
echo.
echo Checking container status...
timeout /t 5 /nobreak >nul
docker-compose ps

echo.
echo ====================================
echo Deployment Complete!
echo ====================================
echo.
echo Services:
echo   Frontend:  http://localhost
echo   Backend:   http://localhost:8080
echo   H2 Console: http://localhost:8080/h2-console
echo.
echo To view logs: docker-compose logs -f
echo To stop:      docker-compose down
echo.

REM Open browser
echo Opening application in browser...
timeout /t 3 /nobreak >nul
start http://localhost

pause
