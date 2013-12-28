@echo off
rem # -*- mode: dos -*-

rem # is debug enabled?
set DEBUG=false

rem # ------------------------------------------------------------ INSTALL_DIR

rem # TODO: Ask IzPack for substituing it directly at install time...?
set INSTALL_DIR_ORIG=%~dp0
set INSTALL_DIR=%INSTALL_DIR_ORIG:\=/%..
if exist %INSTALL_DIR% goto install_dir_exists
    echo INTERNAL ERROR: The install directory does not exist: %INSTALL_DIR%
    goto end
fi
:install_dir_exists

if "%DEBUG%" == "false" goto dont_display_install_dir
    echo DEBUG: Install dir: %INSTALL_DIR%
    echo.
:dont_display_install_dir

rem # ------------------------------------------------------------------- JAVA

rem # TODO: Try to use JAVA_HOME?
set JAVA=java

rem # ------------------------------ EXPATH_PKG_REPO_JAR, EXPATH_PKG_SAXON_JAR

if defined EXPATH_PKG_REPO_JAR goto pkg_repo_jar_defined
    set EXPATH_PKG_REPO_JAR=%INSTALL_DIR%/expath/pkg-java.jar
:pkg_repo_jar_defined

if exist %EXPATH_PKG_REPO_JAR% goto pkg_repo_jar_exists
    echo ERROR: EXPath pkg-java jar does not exist: %EXPATH_PKG_REPO_JAR%
    goto end
:pkg_repo_jar_exists

if defined EXPATH_PKG_SAXON_JAR goto pkg_saxon_jar_defined
    set EXPATH_PKG_SAXON_JAR=%INSTALL_DIR%/expath/pkg-saxon.jar
:pkg_saxon_jar_defined

if exist %EXPATH_PKG_SAXON_JAR% goto pkg_saxon_jar_exists
    echo ERROR: EXPath pkg-saxon jar does not exist: %EXPATH_PKG_SAXON_JAR%
    goto end
:pkg_saxon_jar_exists

rem # --------------------------------------------------- SAXON_HOME, SAXON_CP

if defined SAXON_CP goto saxon_cp_defined

    if defined SAXON_HOME goto saxon_home_defined
        set SAXON_HOME=%INSTALL_DIR%/saxon
    :saxon_home_defined

    if exist %SAXON_HOME% goto saxon_home_exists
        echo Saxon home does not exist: %SAXON_HOME%
        goto end
    :saxon_home_exists

    if defined SAXON_JAR goto saxon_jar_defined
        set SAXON_JAR=%SAXON_HOME%/saxon9ee.jar
        if exist %SAXON_JAR% goto saxon_jar_exists
            set SAXON_JAR=%SAXON_HOME%/saxon9pe.jar
            if exist %SAXON_JAR% goto saxon_jar_exists
                set SAXON_JAR=%SAXON_HOME%/saxon9he.jar
    :saxon_jar_defined

    if exist %SAXON_JAR% goto saxon_jar_exists
        echo Saxon JAR does not exist: %SAXON_JAR%
        goto end
    :saxon_jar_exists

    set SAXON_CP=%SAXON_JAR%
    set SAXON_CP=%SAXON_CP%;%EXPATH_PKG_REPO_JAR%
    set SAXON_CP=%SAXON_CP%;%EXPATH_PKG_SAXON_JAR%

:saxon_cp_defined

if "%DEBUG%" == "false" goto dont_display_saxon_vars
    echo DEBUG: Saxon home     : %SAXON_HOME%
    echo DEBUG: Saxon jar      : %SAXON_JAR%
    echo DEBUG: Saxon classpath: %SAXON_CP%
    echo.
:dont_display_saxon_vars
