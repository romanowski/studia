cd gen
java -cp ../classes:../testClasses:../lib/bcel-5.2.jar Transform $1
java  $1
cd ..
