@echo off
REM ====================================
REM Build Application Script
REM ====================================

echo.
echo ====================================
echo Building Air Quality Backend...
echo ====================================
echo.

REM Set Java 21 and Maven
set JAVA_HOME=C:\Users\Shashidhar\.jdk\jdk-21.0.8
set MAVEN_HOME=C:\Users\Shashidhar\.maven\maven-3.8.9\apache-maven-3.8.9
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

REM Navigate to backend directory
cd /d "%~dp0air-quality-backend"

REM Show versions
echo Java Version:
java --version
echo.
echo Maven Version:
mvn --version
echo.

REM Build
echo Building project with Maven...
echo.
mvn clean package -DskipTests

echo.
if %ERRORLEVEL% EQU 0 (
    echo ====================================
    echo Build Successful!
    echo ====================================
    echo.
    echo JAR file created at:
    echo %~dp0air-quality-backend\target\air-quality-backend-1.0.0.jar
    echo.
    echo You can now run: start-backend.bat
) else (
    echo ====================================
    echo Build Failed!
    echo ====================================
    echo Please check the error messages above
)

echo.
pause
