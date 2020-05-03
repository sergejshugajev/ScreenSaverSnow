@echo off
echo Compile an executable JAR file (in Windows).
call mvn compile assembly:single

echo Run in the Java machine.
java -jar Snow.jar