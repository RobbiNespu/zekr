rem @echo off

setlocal
set prompt=$$

if JAVA_HOME=="" (
	set JAVA_CMD=""
	goto run
)

set MAIN_CLASS=net.sf.zekr.ZekrMain
set JRE_OPT=-Djava.library.path=lib
set JAVA_CMD=%JAVA_HOME%\bin\java.exe
set CLASS_PATH=bin;lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\commons-collections.jar;lib\velocity-1.4.jar;lib\xml-apis.jar;dist\zekr.jar;

:run
"%JAVA_CMD%" -cp "%CLASS_PATH%" %JRE_OPT% %MAIN_CLASS%

endlocal
