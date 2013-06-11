@echo off

set /p a=Podaj a= 
set /p b=Podaj b= 
set /p c=Podaj c= 
echo Obliczymy teraz delte rownania kwadratowego %a%x^2 + %b%x + %c% = 0
set /a delta=%b%*%b%-4*%a%*%c%

echo Delta wynosi: %delta%



if %delta% gtr 0 (
  echo Uklad ma dwa rozwiazania hehe:
  
  set /a digit=%delta%**(1/2)
  echo Pierwiastek z delty wynosi: %digit%

  set /a "x1=(0-%b%-%digit%)/2/%a%"
  set /a "x2=(0-%b%+%digit%)/2/%a%"
  echo x1 = %x1%
  echo x2 = %x2%
) else if %delta%==0 (
  set /a "x=(0-%b%)/2/%a%"
  echo Ukla ma jedno rozwiazanie:
  echo x = %x%
) else (
  echo Uklad nie ma rozwiazan w zbiorze liczb rzeczywistych
)
