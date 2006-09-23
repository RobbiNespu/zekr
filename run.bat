@echo off

setlocal
set prompt=$$

set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe

if JAVA_HOME=="" (
	set JAVA_CMD="javaw.exe"
	goto continue
)

:continue
set MAIN_CLASS=net.sf.zekr.ZekrMain
set JRE_OPT=-Djava.library.path=lib
set CLASS_PATH=lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\apache-commons.jar;lib\velocity-1.4.jar;lib/velocity-tools-generic-1.2.jar;dist\zekr.jar;

echo Launching Zekr...
:run
"%JAVA_CMD%" -cp "%CLASS_PATH%" %JRE_OPT% %MAIN_CLASS%

endlocal
