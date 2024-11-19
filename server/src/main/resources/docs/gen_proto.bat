@echo off
setlocal enabledelayedexpansion

echo start...
echo.

FOR %%p in (*.proto) do (
	set proto=!proto!%%p
)

echo %proto%

protoc --java_out   C:\Users\Administrator\Desktop ./%proto%

echo.
echo end...
echo.
PAUSE 