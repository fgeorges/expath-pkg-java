@echo off
rem # -*- mode: dos -*-

rem # is debug enabled?
set DEBUG=false

SET ADD_CP=%ADD_CP%;%1

REM IF "%ADD_CP%" == "" GOTO set
REM :add
REM SET ADD_CP=%1;%ADD_CP%
REM GOTO end
REM :set
REM SET ADD_CP=%1
REM :end
