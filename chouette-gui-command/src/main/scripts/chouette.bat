@echo OFF

for %%F in ("%0") do set dirname=%%~dpF
set "options="
set "arguments="

IF  NOT "%1" == "-classpath"  goto :endloop
    set "options=-Xbootclasspath/p:%2"
    copy %dirname%\log4j.properties %2\.
	del /q %dirname%\chouette.properties
    copy %2\chouette.properties %dirname%\.
	shift
	shift
	set "arguments=%1"
:loop
	shift
	if "%1"=="" goto :endloop
	  set "arguments=%arguments% %1"
	  goto :loop
:endloop

if "%arguments%" == "" set "arguments=%*"

cd /D %dirname%

"%JAVA_HOME%\bin\java" %options% -Duser.timezone=UTC -Xmx1200M -jar chouette-gui-command-2.0.3.jar %arguments%
