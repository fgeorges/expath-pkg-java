@echo off

rem # is debug enabled?
set DEBUG=false

rem # load the common definitions in expath-pkg-common.bat
call %~dp0/expath-pkg-common.bat

%JAVA% -jar %INSTALL_DIR%/expath/pkg-java.jar %*
