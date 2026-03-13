@ECHO OFF
SETLOCAL EnableDelayedExpansion

set WRAPPER_DIR=%~dp0.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper JAR...
  set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
  if exist "%WRAPPER_PROPS%" (
    for /f "tokens=1,* delims==" %%A in ('findstr /b /c:"wrapperUrl=" "%WRAPPER_PROPS%"') do (
      if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
    )
  )

  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri \"!WRAPPER_URL!\" -OutFile \"%WRAPPER_JAR%\""
  if errorlevel 1 (
    echo Failed to download Maven Wrapper JAR.
    exit /b 1
  )
)

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%
if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
