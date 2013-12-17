@echo off

REM TODO: Try to use JAVA_HOME?
SET JAVA=java

%JAVA% -jar "%~dp0/../expath/pkg-java.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9
