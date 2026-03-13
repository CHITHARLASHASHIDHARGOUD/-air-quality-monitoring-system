@echo off
REM ====================================
REM Urban Air Quality Monitoring System
REM Backend Startup Script
REM ====================================

setlocal

echo.
echo ====================================
echo Starting Air Quality Backend...
echo ====================================
echo.

REM Resolve paths relative to this script directory
set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
set "BACKEND_DIR=%PROJECT_ROOT%\air-quality-backend"

REM Verify Java version
echo Checking Java version...
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    "%JAVA_HOME%\bin\java.exe" --version
) else (
    java --version
)
echo.

REM Navigate to backend directory
if not exist "%BACKEND_DIR%" (
    echo ERROR: Backend directory not found: %BACKEND_DIR%
    echo Expected structure: project-root\scripts and project-root\air-quality-backend
    exit /b 1
)

cd /d "%BACKEND_DIR%"

REM Check if JAR exists, if not build it
if not exist "target\air-quality-backend-1.0.0.jar" (
    echo JAR file not found. Building application...
    echo.

    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests
    ) else (
        call mvn clean package -DskipTests
    )

    if errorlevel 1 (
        echo ERROR: Build failed.
        exit /b 1
    )
    echo.
)

if not exist "target\air-quality-backend-1.0.0.jar" (
    echo ERROR: JAR not found after build.
    exit /b 1
)

REM Run the application
echo Starting Spring Boot application...
echo Backend will be available at: http://localhost:8080
echo Press Ctrl+C to stop the application
echo.
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    "%JAVA_HOME%\bin\java.exe" -jar target\air-quality-backend-1.0.0.jar
) else (
    java -jar target\air-quality-backend-1.0.0.jar
)

pause
