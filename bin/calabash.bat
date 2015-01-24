@echo off
rem # -*- mode: dos -*-

rem # is debug enabled?
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
rem # EXPATH_TOOLS_JAVA_JAR    the paths to the JAR files for Packaging support
rem # EXPATH_TOOLS_SAXON_JAR   (resp. the generic Java tools, the tools for
rem # EXPATH_PKG_REPO_JAR      Saxon, the on-disk repo manager, the support for
rem # EXPATH_PKG_SAXON_JAR     Saxon, and the support for Calabash)
rem # EXPATH_PKG_CALABASH_JAR


rem # load the common definitions in expath-pkg-common.bat
call %~dp0/expath-pkg-common.bat


rem # ==================== classpath ====================

rem # Calabash main class
if defined CALABASH_MAIN goto calabash_main_defined
    set CALABASH_MAIN=com.xmlcalabash.drivers.Main
:calabash_main_defined

if defined CALABASH_CP goto calabash_cp_defined

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

    rem # pkg-calabash.jar
    if defined EXPATH_PKG_CALABASH_JAR goto calabash_jar_defined
        set EXPATH_PKG_CALABASH_JAR=%INSTALL_DIR%/expath/pkg-calabash.jar
    :calabash_jar_defined
    if exist %EXPATH_PKG_CALABASH_JAR% goto calabash_jar_exists
        echo EXPath pkg-calabash jar does not exist (%EXPATH_PKG_CALABASH_JAR%)
        goto end
    :calabash_jar_exists

    set CALABASH_CP=%CALABASH_JAR%
    set CALABASH_CP=%CALABASH_CP%;%SAXON_JAR%
    set CALABASH_CP=%CALABASH_CP%;%CODEC_JAR%
    set CALABASH_CP=%CALABASH_CP%;%IO_JAR%
    set CALABASH_CP=%CALABASH_CP%;%LOGGING_JAR%
    set CALABASH_CP=%CALABASH_CP%;%HCLIENT_JAR%
    set CALABASH_CP=%CALABASH_CP%;%HCORE_JAR%
    set CALABASH_CP=%CALABASH_CP%;%HMIME_JAR%
    set CALABASH_CP=%CALABASH_CP%;%XRESOLVER_JAR%
    set CALABASH_CP=%CALABASH_CP%;%EXPATH_TOOLS_JAVA_JAR%
    set CALABASH_CP=%CALABASH_CP%;%EXPATH_TOOLS_SAXON_JAR%
    set CALABASH_CP=%CALABASH_CP%;%EXPATH_PKG_REPO_JAR%
    set CALABASH_CP=%CALABASH_CP%;%EXPATH_PKG_SAXON_JAR%
    set CALABASH_CP=%CALABASH_CP%;%EXPATH_PKG_CALABASH_JAR%

    if not defined XCC_JAR goto skip_xcc_jar
        set CALABASH_CP=%CALABASH_CP%;%XCC_JAR%
    :skip_xcc_jar
    if not defined TAGSOUP_JAR goto skip_tagsoup_jar
        set CALABASH_CP=%CALABASH_CP%;%TAGSOUP_JAR%
    :skip_tagsoup_jar
    if not defined ISORELAX_JAR goto skip_isorelax_jar
        set CALABASH_CP=%CALABASH_CP%;%ISORELAX_JAR%
    :skip_isorelax_jar
    if not defined JING_JAR goto skip_jing_jar
        set CALABASH_CP=%CALABASH_CP%;%JING_JAR%
    :skip_jing_jar

    if "%DEBUG%" == "false" goto dont_display_cp
        echo DEBUG: Calabash home   : %CALABASH_HOME%
        echo DEBUG: Calabash jar    : %CALABASH_JAR%
        echo DEBUG: Calabash main   : %CALABASH_MAIN%
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
        echo DEBUG: Tools Java jar  : %EXPATH_TOOLS_JAVA_JAR%
        echo DEBUG: Tools Saxon jar : %EXPATH_TOOLS_SAXON_JAR%
        echo DEBUG: Pkg repo jar    : %EXPATH_PKG_REPO_JAR%
        echo DEBUG: Pkg Saxon jar   : %EXPATH_PKG_SAXON_JAR%
        echo DEBUG: Pkg Calabash jar: %EXPATH_PKG_CALABASH_JAR%
        echo.
    :dont_display_cp

:calabash_cp_defined

rem # JAVA_OPT=
rem # REPO=$EXPATH_REPO

rem # ==================== the options ====================

:parse_options

set curopt=%1

rem # number of parameters "shifted"
rem #set shifted=0

rem # if "++", then stop parsing the options
if "%curopt%"_ == "++"_ (
shift
rem #set /a shifted=%shifted% + 1
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
rem #set /a shifted=%shifted% + 1
set ADD_CP=%ADD_CP%;%1
if "%DEBUG%" == "false" goto dont_display_opt_add_cp
    echo DEBUG: ++add-cp: %ADD_CP%
    echo.
:dont_display_opt_add_cp
goto switch_end

rem # The EXPath Packaging repository
:opt_repo
shift
rem #set /a shifted=%shifted% + 1
set REPO=%1
if "%DEBUG%" == "false" goto dont_display_opt_repo
    echo DEBUG: ++repo: %REPO%
    echo.
:dont_display_opt_repo
goto switch_end

rem # Additional option for the JVM
:opt_java
shift
rem #set /a shifted=%shifted% + 1
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
rem #set /a shifted=%shifted% + 1
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

if "%DEBUG%" == "false" goto dont_display_options
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
:dont_display_options

rem # # ==================== do it! ====================

rem # The complete classpath, with additional entries
set CP=%CALABASH_CP%%ADD_CP%

if "%DEBUG%" == "false" goto dont_display_cp
    echo DEBUG: Classpath: %CP%
    echo.
:dont_display_cp

rem # -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
rem # -Dorg.apache.commons.logging.simplelog.showdatetime=true
rem # -Dorg.apache.commons.logging.simplelog.log.org=TRACE
rem # -Djava.util.logging.config.file=/Users/fgeorges/logging.properties

rem # Do it
%JAVA% ^
    %JAVA_OPT% ^
    -ea -esa ^
    -Dcom.xmlcalabash.xproc-configurer=org.expath.pkg.calabash.PkgConfigurer ^
    -cp %CP% ^
    %CALABASH_MAIN% %1 %2 %3 %4 %5 %6 %7 %8 %9
rem #for /f "tokens=%shifted%*" %%x in ("%*") do ^
rem #    %JAVA% ^
rem #        %JAVA_OPT% ^
rem #        -ea -esa ^
rem #        -Dcom.xmlcalabash.xproc-configurer=org.expath.pkg.calabash.PkgConfigurer ^
rem #        -cp %CP% ^
rem #        %CALABASH_MAIN% %%y

:end
