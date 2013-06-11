%% Author: student
%% Created: 06-12-2011
%% Description: TODO: Add description to myList
-module(myList).

%%
%% Include files
%%

%%
%% Exported Functions
%%
-export([contains/2, duplicate/1, removeInts/1]).

%%
%% API Functions
%%

contains(List, Item) ->
	in_con(List, Item).
	
duplicate(List)->
	in_dup(List).

removeInts(List)->
	in_rem(List).

%%
%% Local Functions
%%

%%in_dup ([]) -> [];

in_rem([]) ->[];

in_rem([H | T]) when is_integer(H) ->
	in_rem(T);

in_rem([H | T])  ->
	[H |in_rem(T)].
	


in_dup ([H | T])-> 
	[H| [H| in_dup(T)]];

in_dup([]) -> [].

	
in_con([], _) -> false;
	
in_con([H | _], H) -> true;

in_con([_ | T], Item) -> in_con(T, Item).