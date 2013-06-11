#!/bin/bash

JVM="-I"$JAVA_HOME/include" -$"$JAVA_HOME/include/linux

echo $JVM

gcc $JVM -fPIC -shared src/edu/amwj/StringUtil.c -o classes/libStringUtil.so
javac -d classes src/edu/amwj/*.java