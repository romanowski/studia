#!/bin/bash
mkdir classes
javac tests/*.java tests/test/*java tests/test/pack/*.java tests/test2/pack/pack2/*.java -d classes

declare -a TEST=('Obiekt' 'Static2' 'Static' 'Test0' 'Test2' 'Test4' 'Various2' 'Various' 'Watek' 'Wyjatek' 'Test' 'test.Test4' 'test.Test5' 'test.pack.Test2' 'test.pack.Test3' 'test2.pack.pack2.Test');

CLASSPATH=../classes:classes:../lib/bcel-5.2.jar

for NAME in ${TEST[@]}
do
	java -cp $CLASSPATH Transform $NAME
done

mkdir modified
mv *.class modified
mv test modified
mv test2 modified

CLASSPATH=../classes:../lib/bcel-5.2.jar:modified

mkdir modifiedOut

for NAME in ${TEST[@]}
do
	echo  '*' $NAME '*'
	echo
	java -cp $CLASSPATH $NAME > modifiedOut/$NAME.out
	diff outs/$NAME.out modifiedOut/$NAME.out
	echo  -------------------
	echo
done
