@echo off

set /p od=Od: 
set /p do=Do: 
set /p krok=Krok: 

for /l %%i in (%od%, %krok%, %do%) do (
  echo %%i%
)
