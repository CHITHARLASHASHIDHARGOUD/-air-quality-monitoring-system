@echo off
REM ====================================
REM Quick Test Script
REM ====================================

echo.
echo ====================================
echo Testing Air Quality API
echo ====================================
echo.

REM Check if backend is running
echo Testing backend health...
curl -s http://localhost:8080/actuator/health
if errorlevel 1 (
    echo.
    echo ERROR: Backend is not running!
    echo Please start the backend first: start-backend.bat
    echo.
    pause
    exit /b 1
)

echo.
echo.
echo ====================================
echo Backend is UP!
echo ====================================
echo.

REM Test endpoints
echo Testing API Endpoints:
echo.

echo [1/5] Latest Air Quality Data:
echo ---------------------------------
curl -s http://localhost:8080/api/air-quality/latest
echo.
echo.

echo [2/5] Recent Readings:
echo ---------------------------------
curl -s "http://localhost:8080/api/air-quality/recent?limit=3"
echo.
echo.

echo [3/5] Weather for London:
echo ---------------------------------
curl -s http://localhost:8080/api/weather/city/London
echo.
echo.

echo [4/5] Combined Data:
echo ---------------------------------
curl -s http://localhost:8080/api/weather/combined
echo.
echo.

echo [5/5] Health Check:
echo ---------------------------------
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo ====================================
echo All Tests Complete!
echo ====================================
echo.
echo Access Points:
echo   Backend API: http://localhost:8080
echo   Frontend:    http://localhost:5500/index.html
echo   H2 Console:  http://localhost:8080/h2-console
echo.

pause
