%% Author: student
%% Created: 06-12-2011
%% Description: TODO: Add description to addressBook
-module(addressBook).

%%
%% Include files
%%

%%
%% Exported Functions
%%
-export([createAddresBook/0, addEmail/4, addPhone/4, addContact/5, findByEmail/2, removeContact/3, removeEmail/4, removePhone/4, getEmails/3, getPhones/3, findByOffice/2, findByPhone/2]).
 
createAddresBook()->
	[].

addContact(Name, Surname, Book, Company, Position) ->
		%%  {                mails, phones}    
	Entry = {{Name, Surname}, [], [], {Company, Position}},
	[Entry | Book].
	  
addEmail(Name, Surname,Email, Book) ->
	replaceOrAdd(fun(A) -> name_match(Name, Surname, A) end, fun(A) -> appendEmail(A, Name, Surname, Email) end, Book).
	
addPhone(Name, Surname,Phone, Book) ->
	replaceOrAdd(fun(A) -> name_match(Name, Surname, A) end, fun(A) -> appendPhone(A, Name, Surname, Phone) end, Book).
	
	

	
removeContact(Name, Surname, Book) ->
	replaceOrAdd(fun(A) -> name_match(Name, Surname, A) end, fun(_) -> null end, Book).

removeEmail(Email, Name, Surname, Book) ->
	replaceOrAdd(fun(A) -> name_match(Name, Surname, A) end, fun(A) -> removeEmail_in(A, Email) end, Book).

removePhone(Phone, Name, Surname, Book) ->
	replaceOrAdd(fun(A) -> name_match(Name, Surname, A) end, fun(A) -> removePhone_in(A, Phone) end, Book).
	
getEmails(Name, Surname, Book)->
	case in_find(fun(A) -> name_match(Name, Surname, A) end, Book)
		of
			{_, Emails, _, _} -> Emails;
			B -> B
		end.	

getPhones(Name, Surname, Book)->
	case in_find(fun(A) -> name_match(Name, Surname, A) end, Book)
		of
			{_, _, Phones, _} -> Phones;
			B -> B
		end.

findByEmail(Email, Book) ->
	in_find(fun(A) -> 	in_list(Email, element(2, A)) end, Book).
	
findByPhone(Phone, Book) ->
	in_find(fun(A) -> 	in_list(Phone, element(3, A)) end, Book).
	
findByOffice(Office, Book) ->
	lists:filter(fun(A) -> officeMatch(Office, A) end, Book).

%%
%% Local Functions
%%


%% czy cos nalezy do listy
in_list(I, [I | _]) ->
	true;
in_list(I, [_ | T])->
	in_list(I, T);
in_list(_, []) -> false.

%% sprawdzanie po nazwisku

name_match(Name, Surname, {{Name,Surname}, _,_, _})->
	true;
name_match(_, _, _)->
	false.

%% officeMatch
officeMatch(Office, {_, _, _, {Office, _}})->
	true;
officeMatch(_, _)->
	false.

%%doajemu Email

appendEmail(null, Name, Surname, Email)->
	{{Name, Surname}, [Email | []], [], {null, null}};
appendEmail({Names, Emails, Phones, Comp},_, _, Email) ->
	{Names, [Email | Emails], Phones, Comp}.	

%% dodajemy Numer telefonu
appendPhone(null, Name, Surname, Phone)->
	{{Name, Surname},[], [Phone | []], {null, null}};
appendPhone({Names, Emails, Phones, Comp},_, _, Phone) ->
	{Names, Emails, [Phone  | Phones], Comp}.
	
%% uswamy email
removeEmail_in({Names, Emails, Phones, Comp}, Mail)->
	{Names, lists:delete(Mail,Emails), Phones, Comp};

removeEmail_in(null, _)-> null.

%% uswamy Phone
removePhone_in({Names, Emails, Phones, Comp}, Phone)->
	{Names, Emails, list:delete(Phone, Phones), Comp};

removePhone_in(null, _)-> null.

%chodzenie po liscie
replaceOrAdd(Case, Function, [A | T]) ->
	case Case(A) of
		true -> case Function(A) of
			null -> T;
			C -> [C | T]
			end;
		false 	-> [A | replaceOrAdd(Case, Function, T)]
	end;
	
replaceOrAdd(_, Function, []) ->
	Ret = Function(null),
	case Ret of
		null -> [];
		R -> [R | []]
	end.
	
%wyszukiwanie	
in_find(Case, [A|T])->
	case Case(A) of
		true -> A;
		false 	-> in_find(Case, T)
	end;

in_find(_, []) ->
	{error, "noSuchUser"}.


