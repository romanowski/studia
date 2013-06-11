JCP=../zad4/classes
export LD_LIBRARY_PATH=$JCP
javac -d out -cp $JCP src/Test.java
java -cp $JCP:out Test