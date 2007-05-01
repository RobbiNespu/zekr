@echo off
setlocal
set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe

if JAVA_HOME=="" (
	set JAVA_CMD="javaw.exe"
	goto continue
)

:continue
set MAIN_CLASS=net.sf.zekr.ZekrMain
set CLASS_PATH=lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\apache-commons.jar;lib\velocity-1.4.jar;lucene-highlighter-2.1.0.jar;lucene-core-2.1.1-dev.jar;dist\zekr.jar;
set VM_ARGS=-Xms10m -Xmx70m
echo Launching Zekr...
:run
"%JAVA_CMD%" %VM_ARGS% -cp "%CLASS_PATH%" %MAIN_CLASS%

endlocal
