@echo off
setlocal enabledelayedexpansion

:: Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed. Please install Java 11 or higher from:
    echo https://adoptium.net/
    echo.
    echo After installing Java, please run this script again.
    pause
    exit /b 1
)

:: Create temporary directory
set "TEMP_DIR=%USERPROFILE%\chat-client"
mkdir "%TEMP_DIR%" 2>nul
cd "%TEMP_DIR%"

:: Create client directory
mkdir client 2>nul

:: Download the client files
echo Downloading chat client...
powershell -Command "& {Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ChatClient.java' -OutFile 'client\ChatClient.java'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ReadThread.java' -OutFile 'client\ReadThread.java'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/WriteThread.java' -OutFile 'client\WriteThread.java'}"

:: Compile the client
echo Compiling chat client...
javac -source 11 -target 11 client\*.java

:: Run the client
echo Starting chat client...
java -Dfile.encoding=UTF-8 -Djava.awt.headless=true -cp . client.ChatClient 167.86.109.247 1234

:: Clean up
cd "%USERPROFILE%"
rmdir /s /q "%TEMP_DIR%" 