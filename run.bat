rem @echo off

setlocal
set prompt=$$

if JAVA_HOME=="" (
	set JAVA_CMD="javaw.exe"
	goto continue
)

set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe

:continue
set MAIN_CLASS=net.sf.zekr.ZekrMain
set JRE_OPT=-Djava.library.path=lib
set CLASS_PATH=lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\commons-collections.jar;lib\velocity-1.4.jar;lib\xml-apis.jar;dist\zekr.jar;

echo launching Zekr...
:run
"%JAVA_CMD%" -cp "%CLASS_PATH%" %JRE_OPT% %MAIN_CLASS%

endlocal
