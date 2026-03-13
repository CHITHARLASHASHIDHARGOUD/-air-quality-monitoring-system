@echo off
REM ====================================
REM Open Air Quality Dashboard
REM ====================================

echo.
echo ====================================
echo Opening Air Quality Dashboard...
echo ====================================
echo.

REM Open index.html in default browser
start "" "index.html"

echo.
echo Dashboard opened in your default browser!
echo.
echo IMPORTANT:
echo - Make sure the backend is running (start-backend.bat)
echo - Wait 20-30 seconds for data to appear
echo - Refresh the page if needed (F5)
echo.
pause
