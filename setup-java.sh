#!/bin/bash
# Script to ensure Java 21 is used for Maven

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "✅ Java configured:"
java -version
echo ""
echo "✅ Maven configured:"
mvn -version
