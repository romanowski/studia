%% Author: student
%% Created: 06-12-2011
%% Description: TODO: Add description to module1
-module(module1).

%%
%% Include files
%%

%%
%% Exported Functions
%%
-export([power/2]).

%%
%% API Functions
%%

power(_, 0) -> 1;

power(A, N) -> A * power(A, N-1).

%%
%% Local Functions
%%

