DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CP=$DIR/out/production/zad3:$DIR/lib/scala-library-2.9.1.jar
java -cp $CP $2 Interpreter $1 
