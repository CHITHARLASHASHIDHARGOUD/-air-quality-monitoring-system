@echo off
setlocal EnableDelayedExpansion

set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

set "JAVA_EXE="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not defined JAVA_EXE set "JAVA_EXE=java"

echo Checking Java runtime...
set "JAVA_VERSION_RAW="
set "JAVA_MAJOR="
set "JAVA_VERSION_FILE=%TEMP%\java-version-%RANDOM%.txt"
"%JAVA_EXE%" -version 2> "%JAVA_VERSION_FILE%"
for /f "tokens=3 delims= " %%v in ('findstr /i "version" "%JAVA_VERSION_FILE%"') do set "JAVA_VERSION_RAW=%%~v"
del /q "%JAVA_VERSION_FILE%" >nul 2>&1

for /f "tokens=1 delims=.-_" %%m in ("%JAVA_VERSION_RAW%") do set "JAVA_MAJOR=%%m"
if "%JAVA_MAJOR%"=="1" (
  for /f "tokens=2 delims=." %%m in ("%JAVA_VERSION_RAW%") do set "JAVA_MAJOR=%%m"
)

if not defined JAVA_MAJOR (
  echo [ERROR] Could not detect Java version. Ensure Java is installed and JAVA_HOME is set.
  exit /b 1
)

if %JAVA_MAJOR% LSS 17 (
  echo [ERROR] Detected Java %JAVA_MAJOR% but this project requires Java 17.
  echo [HINT] Set JAVA_HOME to a JDK 17 installation and open a new terminal.
  exit /b 1
)

echo Java %JAVA_MAJOR% detected. Running Maven Wrapper...
pushd "%PROJECT_DIR%"
call "%PROJECT_DIR%\mvnw.cmd" %*
set "EXIT_CODE=%ERRORLEVEL%"
popd

exit /b %EXIT_CODE%
