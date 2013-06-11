%{
%}

%s A
%%

\"								BEGIN(A);
<A>[^\"]*/\"							printf("Ciag znakow: %s\n", yytext);
([a-z]|[A-Z]|_)([a-z]|[A-Z]|_|[0-9])+ 				printf("Identyfikator: %s\n", yytext);

([0-9]|\.)+([a-d]|[f-w]|[y-z]|_)([a-z]|[A-Z]|_|[0-9]|-)* 		//printf("Smiec: %s\n",  yytext); 


[0-9]+(\.[0-9]+)?e-?[0-9]+					printf("Liczba rzeczywista w formacie naukowym: %s\n", yytext);
0x([0-9]|[a-f])+						printf("Liczba w formacie szesnastkowym: %s\n", yytext);
[0-9]+\.[0-9]+							printf("Liczba rzeczywista: %s\n", yytext);
[0-9]+/(;|\ )							printf("Liczba calkowita: %s\n", yytext);



.|\n	
%%

main() {
  yylex();
  return 0;
 }
