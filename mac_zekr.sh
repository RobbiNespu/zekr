#!/bin/sh

DIR_NAME=`dirname $0`
PATH=DIR_NAME:$PATH
MAIN_CLASS=net.sf.zekr.ZekrMain
JRE_OPT=-Djava.library.path=lib
CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-3.3.0.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/lucene-core-2.1.1-dev.jar:lib/lucene-highlighter-2.1.0.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx70m"

cd "$DIR_NAME"
java $VM_ARGS -XstartOnFirstThread -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS $*
echo Press Command-Q to exit
