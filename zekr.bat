@echo off
setlocal
set JAVA_CMD=%JAVA_HOME%\bin\java.exe

if JAVA_HOME=="" (
	set JAVA_CMD="java.exe"
	goto continue
)

:continue
set MAIN_CLASS=net.sf.zekr.ZekrMain
set CLASS_PATH=lib\log4j-1.2.8.jar;lib\swt.jar;lib\commons-collections-3.2.1.jar;lib\commons-codec-1.3.jar;lib\commons-io-1.4.jar;lib\commons-lang-2.4.jar;lib\commons-logging-1.0.4.jar;lib\commons-configuration-1.5.jar;lib\velocity-1.6.2.jar;lib\lucene-core-2.3.2.jar;lib\lucene-highlighter-2.3.2.jar;lib\lucene-snowball-2.3.2.jar;lib\jlayer-1.0.1.jar;lib\basicplayer-3.0.jar;lib\tritonus-share-0.3.6.jar;lib\jorbis-0.0.17.jar;lib\jspeex-0.9.7.jar;lib\mp3spi-1.9.4.jar;lib\vorbisspi-1.0.3.jar;dist\zekr.jar;
set VM_ARGS=-Xms20m -Xmx80m
:run
"%JAVA_CMD%" %VM_ARGS% -cp "%CLASS_PATH%" %MAIN_CLASS% %*

endlocal
