@echo off

rem # is debug enabled?
set DEBUG=false

rem # If you don't set neither SAXON_HOME nor SAXON_CP, this batch script will
rem # attend to initialize SAXON_HOME to the subdir saxon/ in the install dir.
rem # You need to have installed the whole Saxon distribution included in the
rem # EXPath repo bundle then.
rem #
rem # Set this variable in your environment or here.  This is the complete
rem # classpath to use to launch Saxon, except the JAR files contained in
rem # the repository (those are automatically added).  It must at least
rem # contain the following JAR files:
rem #   - saxon9he.jar     - or any main Saxon JAR from 8.8 to 9.2
rem #   - tools-java.jar   - generic tools for XML on Java
rem #   - tools-saxon.jar  - tools for Saxon
rem #   - pkg-repo.jar     - the EXPath repo manager
rem #   - pkg-saxon.jar    - the EXPath pkg support for Saxon
rem #
rem # SET SAXON_CP=.../some.jar;.../saxon9he.jar;.../pkg-repo.jar;.../pkg-saxon.jar
rem #
rem # Set this variable in your environment or here, if you don't set SAXON_CP.
rem # This is the directory where Saxon is installed.
rem #
rem # SET SAXON_HOME=c:/path/to/saxon/dir


rem # load the common definitions in expath-pkg-common.bat
call %~dp0/expath-pkg-common.bat


rem # By default 'xslt', can also be 'xquery'.
SET SAXON_KIND=xslt
SET MEMORY=512m
rem # TODO: ...
rem # SET PROXY=$FG_PROXY

SET CP=
SET ADD_CP=
SET JAVA_OPT=
rem # Defaults to the environment variable, but can be changed by --repo=...
IF NOT DEFINED EXPATH_REPO GOTO reponotset
SET REPO=%EXPATH_REPO%
:reponotset

:parseoptions
IF "%1"x == "--xsl"x    GOTO optxsl
IF "%1"x == "--xslt"x   GOTO optxsl
IF "%1"x == "--xq"x     GOTO optxq
IF "%1"x == "--xquery"x GOTO optxq
IF "%1"x == "--repo"x   GOTO optrepo
IF "%1"x == "--add-cp"x GOTO optaddcp
IF "%1"x == "--cp"x     GOTO optcp
IF "%1"x == "--mem"x    GOTO optmem
IF "%1"x == "--proxy"x  GOTO optproxy
IF "%1"x == "--java"x   GOTO optjava
IF "%1"x == "--help"x   GOTO opthelp

set curopt=%1
if "%curopt:~0,2%"_ == "--"_ goto optunknown

GOTO launch

rem # XSLT engine
:optxsl
SET SAXON_KIND=xslt
SHIFT
GOTO parseoptions

rem # XQuery engine
:optxq
SET SAXON_KIND=xquery
SHIFT
GOTO parseoptions

rem # The EXPath Packaging repository
:optrepo
SHIFT
rem # TODO: Implement "resolve" to resolve leading "~"...
SET REPO=%1
SHIFT
GOTO parseoptions

rem # Add some path to the class path.  May be repeated.
:optaddcp
SHIFT
rem # TODO: Implement "resolve" to resolve leading "~"...
SET ADD_CP=%ADD_CP%;%1
SHIFT
GOTO parseoptions

rem # Set the class path.  May be repeated.
:optcp
SHIFT
REM TODO: Implement "resolve" to resolve leading "~"...
SET CP=%CP%;%1
SHIFT
GOTO parseoptions

rem # The memory space to give to the JVM
:optmem
SHIFT
SET MEMORY=%1
SHIFT
GOTO parseoptions

rem # Add support for --proxy=user:password@host:port
:optproxy
SHIFT
SET PROXY=%1
SHIFT
GOTO parseoptions

rem # Additional option for the JVM
:optjava
SHIFT
SET JAVA_OPT=%JAVA_OPT% %1
SHIFT
GOTO parseoptions

rem # Help message.
:opthelp
echo.
echo Usage: saxon [script options] [processor options]
echo.
echo [processor options] are any option accepted by the original command-line
echo Saxon frontend.  Script options are (all are optional, those marked with
echo an * are repeatable):
echo.
echo   --help                    display this help message
echo   --xslt   or --xsl         invoke Saxon as an XSLT processor (the default)
echo   --xquery or --xq          invoke Saxon as an XQuery processor
echo   --repo ...                set the EXPath Packaging repository dir
echo   --add-cp classpath *      add an entry to the classpath
echo   --cp classpath *          set the classpath (override the default classpath)
echo   --java ...                add an option to the Java Virtual Machine
echo   --mem ...                 set the memory (shortcut for --java=-Xmx...)
echo   --proxy [user:password@]host:port
echo                             HTTP and HTTPS proxy information (not implemented)
echo.
GOTO end

rem # Unknown option!
:optunknown
echo "Unknown option: %1"
GOTO end

:launch

IF NOT DEFINED REPO GOTO repoend
SET JAVA_OPT=%JAVA_OPT% -Dorg.expath.pkg.saxon.repo=%REPO%
rem # Analyse $REPO/*/.saxon/classpath.txt to add JAR files into
rem # the classpath.
FOR /D %%d in (%REPO%/*) DO IF EXIST %%d/.saxon/classpath.txt FOR /F %%p in (%%d/.saxon/classpath.txt) DO CALL saxon_addenv %%p
:repoend

IF NOT DEFINED PROXY GOTO proxyend
rem # TODO: Implement proxy config support.
echo TODO: Proxy config not supported yet in this script...
:proxyend

rem # TODO: Implement real processing instead...
rem # echo "JAVA:       %JAVA%"
rem # echo "SAXON_CP:   %SAXON_CP%"
rem # echo "SAXON_HOME: %SAXON_HOME%"
rem # echo "SAXON_KIND: %SAXON_KIND%"
rem # echo "REPO:       %REPO%"
rem # echo "ADD_CP:     %ADD_CP%"
rem # echo "CP:         %CP%"
rem # echo "MEMORY:     %MEMORY%"
rem # echo "PROXY:      %PROXY%"
rem # echo "JAVA_OPT:   %JAVA_OPT%"

rem # The main Java class to use.
IF "%SAXON_KIND%"x == "xslt"x GOTO kindxslt
SET MAIN_CLASS=net.sf.saxon.Query
GOTO kinddone
:kindxslt
SET MAIN_CLASS=net.sf.saxon.Transform
:kinddone

SET CP=%SAXON_CP%%ADD_CP%
SET INIT=-init:org.expath.pkg.saxon.PkgInitializer
rem # TODO: Add logging options?
%JAVA% -Xmx%MEMORY% %JAVA_OPT% -ea -esa -cp "%CP%" %MAIN_CLASS% %INIT% %*

:end
