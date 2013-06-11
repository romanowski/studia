@echo off
:begin
choice /C:TNM Idziemy na piwo (Tak, Nie, Moze)?
goto lab%ERRORLEVEL%

:lab1
echo Super!
goto ennd

:lab2
echo Eee
goto ennd

:lab3
echo Decyduj sie, wiec jak?
goto begin

:ennd
