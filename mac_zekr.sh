#!/bin/sh

DIR_NAME=`dirname $0`
PATH=DIR_NAME:$PATH
MAIN_CLASS=net.sf.zekr.ZekrMain
JRE_OPT=-Djava.library.path=lib
CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt.jar:lib/commons-collections-3.2.1.jar:lib/commons-codec-1.3.jar:lib/commons-io-1.4.jar:lib/commons-lang-2.4.jar:lib/commons-logging-1.0.4.jar:lib/commons-configuration-1.5.jar:lib/velocity-1.6.2.jar:lib/lucene-core-2.3.2.jar:lib/lucene-highlighter-2.3.2.jar:lib/lucene-snowball-2.3.2.jar:lib/mp3spi-1.9.4.jar:lib/vorbisspi-1.0.3.jar:lib/jlayer-1.0.1.jar:lib/basicplayer-3.0.jar:lib/tritonus-share-0.3.6.jar:lib/tritonus-jorbis-0.3.6.jar:lib/jorbis-0.0.17.jar:lib/jspeex-0.9.7.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx80m"
APP_ARGS=`$*`

cd "$DIR_NAME"
java $VM_ARGS -XstartOnFirstThread -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS $APP_ARGS
echo Press Command-Q to exit
