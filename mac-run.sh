#!/bin/sh

DIR_NAME=`dirname $0`
PATH=DIR_NAME:$PATH
MAIN_CLASS=net.sf.zekr.ZekrMain
JRE_OPT=-Djava.library.path=lib
CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-mac.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/velocity-tools-generic-1.2.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx60m"

cd $DIR_NAME
java $VM_ARGS -XstartOnFirstThread -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
echo Press Command-Q to exit
