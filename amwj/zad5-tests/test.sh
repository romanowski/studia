rm First.class Second.class
javac   -d . src/*
javac -cp ../zad5/lib/bcel-5.2.jar -d baseClasses ../zad5/src/*
java -cp classes:baseClasses:../zad5/lib/bcel-5.2.jar ProblemSixSolver First Second
java First
