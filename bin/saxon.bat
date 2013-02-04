@echo off

REM If you don't set neither SAXON_HOME nor SAXON_CP, this batch script will
REM attend to initialize SAXON_HOME to the subdir saxon/ in the install dir.
REM You need to have installed the whole Saxon distribution included in the
REM EXPath repo bundle then.

REM Set this variable in your environment or here.  This is the complete
REM classpath to use to launch Saxon, except the JAR files contained in
REM the repository (those are automatically added).  It must at least
REM contain the following JAR files:
REM   - saxon9he.jar  (or any main Saxon JAR from 8.8 to 9.2)
REM   - pkg-repo.jar  (the EXPath repo manager)
REM   - pkg-saxon.jar (the EXPath pkg support for Saxon)
REM
REM SET SAXON_CP=.../some.jar;.../saxon9he.jar;.../pkg-repo.jar;.../pkg-saxon.jar

REM Set this variable in your environment or here, if you don't set SAXON_CP.
REM This is the directory where Saxon is installed.
REM
REM SET SAXON_HOME=c:/path/to/saxon/dir

REM TODO: Try to use JAVA_HOME?
SET JAVA=java

IF DEFINED SAXON_CP GOTO saxoncpset

IF DEFINED SAXON_HOME GOTO saxonhomeset

SET SAXON_HOME=%~dp0/../saxon

:saxonhomeset
REM TODO: Test file existence to use only one single JAR.
SET SAXON_CP=%SAXON_HOME%/saxon9ee.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon9pe.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon9he.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon9sa.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon9.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon8sa.jar
SET SAXON_CP=%SAXON_CP%;%SAXON_HOME%/saxon8.jar
SET SAXON_CP=%SAXON_CP%;%~dp0/../expath/pkg-repo.jar
SET SAXON_CP=%SAXON_CP%;%~dp0/../expath/pkg-saxon.jar

:saxoncpset

REM By default 'xslt', can also be 'xquery'.
SET SAXON_KIND=xslt
SET MEMORY=512m
REM TODO: ...
REM SET PROXY=$FG_PROXY

SET CP=
SET ADD_CP=
SET JAVA_OPT=
REM Defaults to the environment variable, but can be changed by --repo=...
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

REM TODO: Test if the option starts with '--'.
REM IF "%1"x == "--*"x       GOTO optunknown

GOTO launch

REM XSLT engine
:optxsl
SET SAXON_KIND=xslt
SHIFT
GOTO parseoptions

REM XQuery engine
:optxq
SET SAXON_KIND=xquery
SHIFT
GOTO parseoptions

REM The EXPath Packaging repository
:optrepo
SHIFT
REM TODO: Implement "resolve" to resolve leading "~"...
SET REPO=%1
SHIFT
GOTO parseoptions

REM Add some path to the class path.  May be repeated.
:optaddcp
SHIFT
REM TODO: Implement "resolve" to resolve leading "~"...
SET ADD_CP=%ADD_CP%;%1
SHIFT
GOTO parseoptions

REM Set the class path.  May be repeated.
:optcp
SHIFT
REM TODO: Implement "resolve" to resolve leading "~"...
SET CP=%CP%;%1
SHIFT
GOTO parseoptions

REM The memory space to give to the JVM
:optmem
SHIFT
SET MEMORY=%1
SHIFT
GOTO parseoptions

REM Add support for --proxy=user:password@host:port
:optproxy
SHIFT
SET PROXY=%1
SHIFT
GOTO parseoptions

REM Additional option for the JVM
:optjava
SHIFT
SET JAVA_OPT=%JAVA_OPT% %1
SHIFT
GOTO parseoptions

REM Help message.
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

REM Unknown option!
:optunknown
echo "Unknown option: %1"
GOTO end

:launch

IF NOT DEFINED REPO GOTO repoend
SET JAVA_OPT=%JAVA_OPT% -Dorg.expath.pkg.saxon.repo=%REPO%
REM Analyse $REPO/*/.saxon/classpath.txt to add JAR files into
REM the classpath.
FOR /D %%d in (%REPO%/*) DO IF EXIST %%d/.saxon/classpath.txt FOR /F %%p in (%%d/.saxon/classpath.txt) DO CALL saxon_addenv %%p
:repoend

IF NOT DEFINED PROXY GOTO proxyend
REM TODO: Implement proxy config support.
echo TODO: Proxy config not supported yet in this script...
:proxyend

REM TODO: Implement real processing instead...
REM echo "JAVA:       %JAVA%"
REM echo "SAXON_CP:   %SAXON_CP%"
REM echo "SAXON_HOME: %SAXON_HOME%"
REM echo "SAXON_KIND: %SAXON_KIND%"
REM echo "REPO:       %REPO%"
REM echo "ADD_CP:     %ADD_CP%"
REM echo "CP:         %CP%"
REM echo "MEMORY:     %MEMORY%"
REM echo "PROXY:      %PROXY%"
REM echo "JAVA_OPT:   %JAVA_OPT%"

REM The main Java class to use.
IF "%SAXON_KIND%"x == "xslt"x GOTO kindxslt
SET MAIN_CLASS=net.sf.saxon.Query
GOTO kinddone
:kindxslt
SET MAIN_CLASS=net.sf.saxon.Transform
:kinddone

SET CP=%SAXON_CP%%ADD_CP%
SET INIT=-init:org.expath.pkg.saxon.PkgInitializer
REM TODO: Add logging options?
%JAVA% -Xmx%MEMORY% %JAVA_OPT% -ea -esa -cp "%CP%" %MAIN_CLASS% %INIT% %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
