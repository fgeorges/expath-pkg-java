@echo off

set DEBUG=false

rem # Launch Calabash, setting up the classpath and support for EXPath Packaging.
rem # 
rem # If you installed the full EXPath Repo distribution, including Saxon and
rem # Calabash themselves, it will find them automatically from the install dir.
rem # If you want to use your own Calabash and/or Saxon install, just set the
rem # environment variables CALABASH_HOME and/or SAXON_HOME.  Generally speaking,
rem # you can set the following environment variables to customize the behaviour
rem # of this script:
rem # 
rem # CALABASH_HOME        the home dir of the Calabash install; must contain a
rem #                      file named calabash.jar
rem # 
rem # CALABASH_JAR         alternatively, instead of CALABASH_HOME, you can
rem #                      directly set the JAR file path
rem # 
rem # SAXON_HOME           the home dir of the Saxon install; must contain a file
rem #                      named saxon9ee.jar, saxon9pe.jar or saxon9he.jar (if
rem #                      several exist, it will pick them in that order)
rem # 
rem # CALABASH_MAIN        the name of the Java class to use as the main driver;
rem #                      the default value com.xmlcalabash.drivers.Main is the
rem #                      official Calabash main, but if you have your own class
rem #                      you can override it here
rem # 
rem # XCC_JAR              the paths to the JAR files for resp. the MarkLogic XCC
rem # TAGSOUP_JAR          lib, the TagSoup lib, and the isorelax and jing JAR
rem # ISORELAX_JAR         for Jing
rem # JING_JAR             
rem # 
rem # EXPATH_PKG_REPO_JAR      the paths to the JAR files for Packaging support
rem # EXPATH_PKG_SAXON_JAR     (resp. the on-disk repo manager, the support for
rem # EXPATH_PKG_CALABASH_JAR  Saxon, and the support for Calabash)


rem # ==================== variables ====================

rem # get the install dir
rem # TODO: Ask IzPack for substituing it directly at install time...?
set inst_dir=%~dp0
set INSTALL_DIR=%inst_dir:\=/%..
if exist %INSTALL_DIR% goto install_dir_exists
    echo INTERNAL ERROR: The install directory is not a directory?!? (%INSTALL_DIR%)
    goto end
fi
:install_dir_exists

if "%DEBUG%" == "false" goto dont_display_install_dir
    echo DEBUG: Install dir: %INSTALL_DIR%
    echo.
:dont_display_install_dir

rem # Calabash home
if defined CALABASH_HOME goto calabash_home_defined
    rem # By default, try calabash/ as CALABASH_HOME in the install dir
    set CALABASH_HOME=%INSTALL_DIR%/calabash
:calabash_home_defined

if exist %CALABASH_HOME% goto calabash_home_exists
    echo Calabash home does not exist (%CALABASH_HOME%)
    goto end
:calabash_home_exists

rem # Calabash JAR
if defined CALABASH_JAR goto calabash_jar_defined
    set CALABASH_JAR=%CALABASH_HOME%/calabash.jar
:calabash_jar_defined

if exist %CALABASH_JAR% goto calabash_jar_exists
    echo Calabash JAR does not exist (%CALABASH_JAR%)
    goto end
:calabash_jar_exists

rem # Calabash main class
if defined CALABASH_MAIN goto calabash_main_defined
    set CALABASH_MAIN=com.xmlcalabash.drivers.Main
:calabash_main_defined

if "%DEBUG%" == "false" goto dont_display_calabash_vars
    echo DEBUG: Calabash home: %CALABASH_HOME%
    echo DEBUG: Calabash jar : %CALABASH_JAR%
    echo DEBUG: Calabash main: %CALABASH_MAIN%
    echo.
:dont_display_calabash_vars

rem # Saxon home
if defined SAXON_HOME goto saxon_home_defined
    rem # By default, try saxon/ as SAXON_HOME in the install dir
    set SAXON_HOME=%INSTALL_DIR%/saxon
:saxon_home_defined

if exist %SAXON_HOME% goto saxon_home_exists
    echo Saxon home does not exist (%SAXON_HOME%)
    goto end
:saxon_home_exists

rem # Saxon JAR
if defined SAXON_JAR goto saxon_jar_defined
    set SAXON_JAR=%SAXON_HOME%/saxon9ee.jar
    if exist %SAXON_JAR% goto saxon_jar_exists
        set SAXON_JAR=%SAXON_HOME%/saxon9pe.jar
        if exist %SAXON_JAR% goto saxon_jar_exists
            set SAXON_JAR=%SAXON_HOME%/saxon9he.jar
:saxon_jar_defined

if exist %SAXON_JAR% goto saxon_jar_exists
    echo Saxon JAR does not exist (%SAXON_JAR%)
    goto end
:saxon_jar_exists

if "%DEBUG%" == "false" goto dont_display_saxon_vars
    echo DEBUG: Saxon home: %SAXON_HOME%
    echo DEBUG: Saxon jar : %SAXON_JAR%
    echo.
:dont_display_saxon_vars

rem # Libraries
set CODEC_JAR=%CALABASH_HOME%/lib/commons-codec-1.6.jar
set IO_JAR=%CALABASH_HOME%/lib/commons-io-1.3.1.jar
set LOGGING_JAR=%CALABASH_HOME%/lib/commons-logging-1.1.1.jar
set HCLIENT_JAR=%CALABASH_HOME%/lib/httpclient-4.2.5.jar
set HCORE_JAR=%CALABASH_HOME%/lib/httpcore-4.2.4.jar
set HMIME_JAR=%CALABASH_HOME%/lib/httpmime-4.2.5.jar
set XRESOLVER_JAR=%CALABASH_HOME%/lib/xmlresolver.jar
if defined XCC_JAR goto xcc_jar_defined
    if not exist %CALABASH_HOME%/lib/xcc.jar goto xcc_jar_defined
        set XCC_JAR=%CALABASH_HOME%/lib/xcc.jar
:xcc_jar_defined
if defined TAGSOUP_JAR goto tagsoup_jar_defined
    if not exist %CALABASH_HOME%/lib/tagsoup-1.2.jar goto tagsoup_jar_defined
        set TAGSOUP_JAR=%CALABASH_HOME%/lib/tagsoup-1.2.jar
:tagsoup_jar_defined
if defined ISORELAX_JAR goto isorelax_jar_defined
    if not exist %CALABASH_HOME%/lib/isorelax.jar goto isorelax_jar_defined
        set ISORELAX_JAR=%CALABASH_HOME%/lib/isorelax.jar
:isorelax_jar_defined
if defined JING_JAR goto jing_jar_defined
    if not exist %CALABASH_HOME%/lib/jing.jar goto jing_jar_defined
        set JING_JAR=%CALABASH_HOME%/lib/jing.jar
:jing_jar_defined

if "%DEBUG%" == "false" goto dont_display_lib_vars
    echo DEBUG: Codec jar       : %CODEC_JAR%
    echo DEBUG: Commons IO jar  : %IO_JAR%
    echo DEBUG: Logging jar     : %LOGGING_JAR%
    echo DEBUG: HTTP Client jar : %HCLIENT_JAR%
    echo DEBUG: HTTP Core jar   : %HCORE_JAR%
    echo DEBUG: HTTP MIME jar   : %HMIME_JAR%
    echo DEBUG: XCC jar         : %XCC_JAR%
    echo DEBUG: TagSoup jar     : %TAGSOUP_JAR%
    echo DEBUG: ISO Relax jar   : %ISORELAX_JAR%
    echo DEBUG: Jing jar        : %JING_JAR%
    echo DEBUG: XML Resolver jar: %XRESOLVER_JAR%
    echo.
:dont_display_lib_vars

rem # The EXPath JAR files for packaging support
rem # pkg-repo.jar
if defined EXPATH_PKG_REPO_JAR goto repo_jar_defined
    set EXPATH_PKG_REPO_JAR=%INSTALL_DIR%/expath/pkg-repo.jar
:repo_jar_defined
if exist %EXPATH_PKG_REPO_JAR% goto repo_jar_exists
    echo EXPath pkg-repo jar does not exist (%EXPATH_PKG_REPO_JAR%)
    goto end
:repo_jar_exists

rem # pkg-saxon.jar
if defined EXPATH_PKG_SAXON_JAR goto saxon_jar_defined
    set EXPATH_PKG_SAXON_JAR=%INSTALL_DIR%/expath/pkg-saxon.jar
:saxon_jar_defined
if exist %EXPATH_PKG_SAXON_JAR% goto saxon_jar_exists
    echo EXPath pkg-saxon jar does not exist (%EXPATH_PKG_SAXON_JAR%)
    goto end
:saxon_jar_exists

rem # pkg-calabash.jar
if defined EXPATH_PKG_CALABASH_JAR goto calabash_jar_defined
    set EXPATH_PKG_CALABASH_JAR=%INSTALL_DIR%/expath/pkg-calabash.jar
:calabash_jar_defined
if exist %EXPATH_PKG_CALABASH_JAR% goto calabash_jar_exists
    echo EXPath pkg-calabash jar does not exist (%EXPATH_PKG_CALABASH_JAR%)
    goto end
:calabash_jar_exists

if "%DEBUG%" == "false" goto dont_display_expath_vars
    echo DEBUG: EXPath pkg-repo jar    : %EXPATH_PKG_REPO_JAR%
    echo DEBUG: EXPath pkg-saxon jar   : %EXPATH_PKG_SAXON_JAR%
    echo DEBUG: EXPath pkg-calabash jar: %EXPATH_PKG_CALABASH_JAR%
    echo.
:dont_display_expath_vars

rem # # The classpath delimiter
rem # if uname | grep -i cygwin >/dev/null 2>&1; then
rem #     CP_DELIM=";"
rem # else
rem #     CP_DELIM=":"
rem # fi
rem # 
rem # CP=
rem # JAVA_OPT=
rem # REPO=$EXPATH_REPO

rem # ================= the initial classpath =================

set CP=%CALABASH_JAR%
set CP=%CP%;%SAXON_JAR%
set CP=%CP%;%CODEC_JAR%
set CP=%CP%;%IO_JAR%
set CP=%CP%;%LOGGING_JAR%
set CP=%CP%;%HCLIENT_JAR%
set CP=%CP%;%HCORE_JAR%
set CP=%CP%;%HMIME_JAR%
set CP=%CP%;%XCC_JAR%
set CP=%CP%;%TAGSOUP_JAR%
set CP=%CP%;%ISORELAX_JAR%
set CP=%CP%;%JING_JAR%
set CP=%CP%;%XRESOLVER_JAR%
set CP=%CP%;%EXPATH_PKG_REPO_JAR%
set CP=%CP%;%EXPATH_PKG_SAXON_JAR%
set CP=%CP%;%EXPATH_PKG_CALABASH_JAR%

if "%DEBUG%" == "false" goto dont_display_cp
    echo DEBUG: Classpath: %CP%
    echo.
:dont_display_cp

rem # ==================== the options ====================

:parse_options

set curopt=%1

rem # if "++", then stop parsing the options
if "%curopt%"_ == "++"_ (
shift
goto options_parsed
)

rem # if not starting with "++" anymore, we're done parsing the options
if "%curopt:~0,2%"_ == "++"_ goto switch_option
goto options_parsed

:switch_option
if "%curopt%"_ == "++add-cp"_ goto opt_add_cp
if "%curopt%"_ == "++repo"_   goto opt_repo
if "%curopt%"_ == "++java"_   goto opt_java
if "%curopt%"_ == "++help"_   goto opt_help
goto optunknown

rem # Add some path to the class path.  May be repeated.
:opt_add_cp
shift
set ADD_CP=%ADD_CP%;%1
if "%DEBUG%" == "false" goto dont_display_opt_add_cp
    echo DEBUG: ++add-cp: %ADD_CP%
    echo.
:dont_display_opt_add_cp
goto switch_end

rem # The EXPath Packaging repository
:opt_repo
shift
set REPO=%1
if "%DEBUG%" == "false" goto dont_display_opt_repo
    echo DEBUG: ++repo: %REPO%
    echo.
:dont_display_opt_repo
goto switch_end

rem # Additional option for the JVM
:opt_java
shift
set JAVA_OPT=%JAVA_OPT% %1
if "%DEBUG%" == "false" goto dont_display_opt_java
    echo DEBUG: ++java: %JAVA_OPT%
    echo.
:dont_display_opt_java
goto switch_end

rem # Help message
:opt_help
echo.
echo Usage: calabash ^<script options^> ^<processor options^>
echo.
echo Processor options are any option accepted by the original command-line
echo Calabash frontend.  Script options are (all are optional, those marked with
echo an * are repeatable, those with ... require a parameter):
echo.
echo  ++help                    display this help message
echo  ++repo ...                the path to the packaging repository to use
echo  ++java ... *              add an option to the Java Virtual Machine
echo  ++add-cp ... *            add an entry to the classpath
echo.
goto end

rem # Unknown option!
:optunknown
echo ERROR: Unknown option: "%curopt%"
goto end

:switch_end
shift
goto parse_options

goto options_parsed

:options_parsed

rem # # ==================== EXPath repo ====================

if "%REPO%"_ == ""_ goto no_repo

rem # the repo itself
set JAVA_OPT=%JAVA_OPT% -Dorg.expath.pkg.calabash.repo=%REPO%

rem # TODO: add each line of {repo}/*/.calabash/classpath.txt to the classpath
rem # TODO: Shouldn't we add the extensions for Saxon as well...?

:no_repo

if "%DEBUG%" == "false" goto dont_display_vars
    echo DEBUG: JAVA_OPT: %JAVA_OPT%
    echo DEBUG: ADD_CP  : %ADD_CP%
    echo DEBUG: CP      : %CP%
    echo DEBUG: arg #1  : %1
    echo DEBUG: arg #2  : %2
    echo DEBUG: arg #3  : %3
    echo DEBUG: arg #4  : %4
    echo DEBUG: arg #5  : %5
    echo DEBUG: arg #6  : %6
    echo DEBUG: arg #7  : %7
    echo DEBUG: arg #8  : %8
    echo DEBUG: arg #9  : %9
    echo.
:dont_display_vars

rem # # ==================== do it! ====================

rem # The complete classpath, with additional entries
set CP=%CP%%ADD_CP%

rem # -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
rem # -Dorg.apache.commons.logging.simplelog.showdatetime=true
rem # -Dorg.apache.commons.logging.simplelog.log.org=TRACE
rem # -Djava.util.logging.config.file=/Users/fgeorges/logging.properties

rem # Do it
java ^
    %JAVA_OPT% ^
    -ea -esa ^
    -Dcom.xmlcalabash.xproc-configurer=org.expath.pkg.calabash.PkgConfigurer ^
    -cp %CP% ^
    %CALABASH_MAIN% %*

:end
