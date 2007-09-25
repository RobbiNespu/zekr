@echo off
setlocal
set JAVA_CMD=%JAVA_HOME%\bin\java.exe

if JAVA_HOME=="" (
	set JAVA_CMD="java.exe"
	goto continue
)

:continue
set MAIN_CLASS=net.sf.zekr.ZekrMain
set CLASS_PATH=lib\log4j-1.2.8.jar;lib\swt-3.3.0.jar;lib\apache-commons.jar;lib\velocity-1.4.jar;lib\lucene-highlighter-2.2.0.jar;lib\lucene-core-2.2.0.jar;dist\zekr.jar;
set VM_ARGS=-Xms10m -Xmx70m
:run
"%JAVA_CMD%" %VM_ARGS% -cp "%CLASS_PATH%" %MAIN_CLASS% %*

endlocal
