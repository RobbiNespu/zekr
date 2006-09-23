#!/bin/sh

export MAIN_CLASS=net.sf.zekr.ZekrMain
export JRE_OPT=-Djava.library.path=lib
export CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-mac.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/velocity-tools-generic-1.2.jar:dist/zekr.jar

echo Launching Zekr...
java -XstartOnFirstThread -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
