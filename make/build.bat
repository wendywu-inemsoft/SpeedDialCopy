cd ..\bin
call mkenv

cd ..

javac -sourcepath src -d classes src\*.java
copy src\log4j.properties classes\log4j.properties
pause